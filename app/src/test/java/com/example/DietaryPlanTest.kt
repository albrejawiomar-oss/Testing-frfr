package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FitnessRepository
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DietaryPlanTest {

    @Test
    fun `generatePlan computes accurate macros and applies freemium gating for free tier`() {
        val deficiencies = listOf(
            NutrientDeficiency.VITAMIN_D3,
            NutrientDeficiency.MAGNESIUM,
            NutrientDeficiency.ZINC
        )

        val plan = DietaryPlanGenerator.generatePlan(
            dietType = DietType.OMNIVORE,
            goal = FitnessDietGoal.LEAN_HYPERTROPHY,
            mealFrequency = MealFrequency.FOUR_FIVE_MEALS,
            allergies = listOf("None"),
            waterIntakeRating = "2.5L / Optimal Hydration",
            selectedDeficiencies = deficiencies,
            userWeightKg = 75.0,
            isPro = false
        )

        // Baseline + 300 kcal for hypertrophy
        assertTrue("Calories should be above 2000", plan.targetCalories >= 2000)
        assertTrue("Protein should be calibrated to hypertrophy goal", plan.targetProtein >= 140)
        assertFalse("Plan should reflect free tier", plan.isProUnlocked)

        // Freemium check on deficiencies: 1st free, others locked
        assertEquals(3, plan.deficiencies.size)
        assertFalse("First deficiency should be free", plan.deficiencies[0].isProExclusive)
        assertTrue("Second deficiency should be pro exclusive", plan.deficiencies[1].isProExclusive)
        assertTrue("Third deficiency should be pro exclusive", plan.deficiencies[2].isProExclusive)

        // Freemium check on meals: first 2 free, remaining locked
        assertTrue("Meals should not be empty", plan.meals.isNotEmpty())
        assertFalse("First meal should be free", plan.meals[0].isProExclusive)
        assertFalse("Second meal should be free", plan.meals[1].isProExclusive)
        if (plan.meals.size > 2) {
            assertTrue("Subsequent meals should be pro exclusive", plan.meals[2].isProExclusive)
        }
    }

    @Test
    fun `generatePlan unlocks all deficiencies and meals when user is Pro`() {
        val deficiencies = listOf(
            NutrientDeficiency.VITAMIN_D3,
            NutrientDeficiency.MAGNESIUM,
            NutrientDeficiency.OMEGA_3
        )

        val proPlan = DietaryPlanGenerator.generatePlan(
            dietType = DietType.KETOGENIC,
            goal = FitnessDietGoal.FAT_LOSS_CUT,
            mealFrequency = MealFrequency.INTERMITTENT_FASTING,
            allergies = listOf("Dairy / Lactose"),
            waterIntakeRating = "3.5L+ (Heavy Training)",
            selectedDeficiencies = deficiencies,
            userWeightKg = 80.0,
            isPro = true
        )

        assertTrue(proPlan.isProUnlocked)
        // All deficiencies unlocked in Pro
        assertTrue("All deficiencies should be unlocked in Pro", proPlan.deficiencies.none { it.isProExclusive })
        // All meals unlocked in Pro
        assertTrue("All meals should be unlocked in Pro", proPlan.meals.none { it.isProExclusive })
    }

    @Test
    fun `dietary plan JSON serialization and deserialization roundtrip`() {
        val originalPlan = DietaryPlanGenerator.generatePlan(
            dietType = DietType.VEGAN,
            goal = FitnessDietGoal.ENDURANCE_PEAK,
            mealFrequency = MealFrequency.THREE_MEALS,
            allergies = listOf("Gluten / Wheat", "Peanuts & Tree Nuts"),
            waterIntakeRating = "2.5L / Optimal Hydration",
            selectedDeficiencies = listOf(NutrientDeficiency.VITAMIN_B12, NutrientDeficiency.IRON_FERRITIN),
            userWeightKg = 70.0,
            isPro = false
        )

        val jsonString = originalPlan.toJson()
        assertNotNull(jsonString)
        assertTrue(jsonString.contains("VEGAN"))
        assertTrue(jsonString.contains("ENDURANCE_PEAK"))

        val deserialized = DietaryPlan.fromJson(jsonString)
        assertNotNull(deserialized)
        assertEquals(originalPlan.id, deserialized!!.id)
        assertEquals(originalPlan.dietType, deserialized.dietType)
        assertEquals(originalPlan.goal, deserialized.goal)
        assertEquals(originalPlan.targetCalories, deserialized.targetCalories)
        assertEquals(originalPlan.targetProtein, deserialized.targetProtein)
        assertEquals(originalPlan.targetCarbs, deserialized.targetCarbs)
        assertEquals(originalPlan.targetFats, deserialized.targetFats)
        assertEquals(originalPlan.deficiencies.size, deserialized.deficiencies.size)
        assertEquals(originalPlan.meals.size, deserialized.meals.size)
    }

    @Test
    fun `repository saves and applies dietary plan to active tracker and daily routines`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = FitnessRepository(context)

        val plan = DietaryPlanGenerator.generatePlan(
            dietType = DietType.HIGH_PROTEIN_CARNIVORE,
            goal = FitnessDietGoal.LEAN_HYPERTROPHY,
            mealFrequency = MealFrequency.FOUR_FIVE_MEALS,
            allergies = emptyList(),
            waterIntakeRating = "2.5L / Optimal Hydration",
            selectedDeficiencies = listOf(NutrientDeficiency.ELECTROLYTES),
            userWeightKg = 85.0,
            isPro = true
        )

        repository.applyDietaryPlanToTracker(plan)

        val activeGoals = repository.nutritionGoals.value
        assertEquals(plan.targetCalories, activeGoals.targetCalories)
        assertEquals(plan.targetProtein, activeGoals.targetProtein)
        assertEquals(plan.targetCarbs, activeGoals.targetCarbs)
        assertEquals(plan.targetFats, activeGoals.targetFats)

        val activePlan = repository.dietaryPlan.value
        assertNotNull(activePlan)
        assertEquals(plan.id, activePlan!!.id)
    }
}
