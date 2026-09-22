package com.example.model

import java.util.UUID

enum class DietType(
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val description: String
) {
    OMNIVORE("Omnivore Standard", "Balanced animal & plant whole foods", "🥩", "Classic balanced bioavailable nutrition"),
    HIGH_PROTEIN_CARNIVORE("High-Protein / Carnivore", "Animal-forward dense nutrition", "🍗", "Focus on lean meats, poultry, wild fish & eggs"),
    MEDITERRANEAN("Mediterranean", "Seafood, olive oil, greens & legumes", "🫒", "Rich in monounsaturated fats, polyphenols & omega-3"),
    VEGETARIAN("Vegetarian", "Plant-forward + eggs & dairy", "🧀", "Plant-based with Greek yogurt, cottage cheese & eggs"),
    VEGAN("Vegan / 100% Plant", "Pure plant-powered cellular fuels", "🌱", "Plant protein synergy with focused B12 & iron supplementation"),
    KETOGENIC("Ketogenic", "High healthy fats, ultra-low carb (<35g)", "🥑", "Ketone fueling with high electrolyte retention demands"),
    LOW_CARB_PALEO("Paleo / Ancestral", "Whole unprocessed primal roots & meats", "🥥", "Grain-free, dairy-free clean whole foods"),
    PESCATARIAN("Pescatarian", "Plant-forward + wild seafood", "🐟", "Abundant marine omega-3 and clean protein")
}

enum class FitnessDietGoal(
    val title: String,
    val description: String,
    val calorieAdjustment: Int,
    val proteinPerKg: Double,
    val badge: String
) {
    FAT_LOSS_CUT("Aggressive Fat Shred", "Caloric deficit designed to protect lean mass", -450, 2.2, "🔥 -450 kcal"),
    LEAN_HYPERTROPHY("Lean Hypertrophy Surplus", "Surplus energy maximizing protein synthesis", +300, 2.0, "🦾 +300 kcal"),
    BODY_RECOMPOSITION("Metabolic Recomposition", "Maintenance calories to burn fat & build muscle", 0, 2.2, "⚡ Recomp"),
    ENDURANCE_PEAK("Endurance & Stamina", "Carbohydrate replenishment for athletic output", +150, 1.8, "🏃 Glycogen")
}

enum class MealFrequency(val title: String, val desc: String) {
    THREE_MEALS("3 Balanced Meals", "Morning, midday, and evening sustained energy"),
    FOUR_FIVE_MEALS("4-5 Frequent Surges", "Frequent protein pulses every 3-4 hours"),
    INTERMITTENT_FASTING("Intermittent Fasting (16:8)", "16-hour metabolic rest with 8-hour feeding window")
}

enum class NutrientDeficiency(
    val id: String,
    val nutrientName: String,
    val icon: String,
    val keySymptoms: String,
    val therapeuticDose: String,
    val primaryFoodSources: List<String>,
    val synergyTip: String,
    val recommendedTiming: String
) {
    VITAMIN_D3(
        id = "vit_d3",
        nutrientName = "Vitamin D3 (Cholecalciferol)",
        icon = "☀️",
        keySymptoms = "Chronic low energy, seasonal winter fatigue, bone/joint ache, slow recovery",
        therapeuticDose = "2,000 - 4,000 IU daily",
        primaryFoodSources = listOf("Wild Alaskan salmon", "Pastured egg yolks", "UV-exposed mushrooms", "Fortified dairy/plant milk"),
        synergyTip = "Must be paired with Vitamin K2 (MK-7) to direct calcium safely into bones and prevent vascular calcification.",
        recommendedTiming = "Morning with first dietary fat source (fat-soluble)"
    ),
    MAGNESIUM(
        id = "magnesium",
        nutrientName = "Magnesium (Glycinate / Malate)",
        icon = "⚡",
        keySymptoms = "Muscle spasms, twitches, restless legs, sleep onset delay, workout tightness",
        therapeuticDose = "350 - 450 mg elemental magnesium",
        primaryFoodSources = listOf("Raw pumpkin seeds", "85%+ dark chocolate", "Steamed spinach", "Hass avocados", "Black beans"),
        synergyTip = "Magnesium Glycinate binds to glycine for deep neurological calmness and parasympathetic activation.",
        recommendedTiming = "45 minutes prior to bedtime"
    ),
    IRON_FERRITIN(
        id = "iron",
        nutrientName = "Iron & Ferritin (Oxygen Carrier)",
        icon = "🩸",
        keySymptoms = "Pale skin, sudden dizziness on heavy lifts, breathlessness, chronic cold hands",
        therapeuticDose = "18 - 45 mg elemental iron (or 120g beef liver)",
        primaryFoodSources = listOf("Grass-fed lean beef", "Cooked black lentils", "Sprouted pumpkin seeds", "Steamed spinach"),
        synergyTip = "Co-ingest with Vitamin C (citrus, bell peppers) to boost absorption by up to 300%. Avoid coffee and calcium for 2 hours.",
        recommendedTiming = "Mid-morning on an empty stomach or with Vitamin C"
    ),
    VITAMIN_B12(
        id = "b12",
        nutrientName = "Vitamin B12 (Methylcobalamin)",
        icon = "🧬",
        keySymptoms = "Brain fog, memory slips, hand/foot tingling, extreme lethargy on plant diets",
        therapeuticDose = "1,000 mcg sublingual weekly or 250 mcg daily",
        primaryFoodSources = listOf("Fortified nutritional yeast", "Wild sardines", "Beef liver", "Organic eggs"),
        synergyTip = "Sublingual methylcobalamin directly enters capillaries, bypassing stomach acid and intrinsic factor limitations.",
        recommendedTiming = "Morning with water"
    ),
    ZINC(
        id = "zinc",
        nutrientName = "Zinc (Picolinate / Bisglycinate)",
        icon = "🛡️",
        keySymptoms = "Prolonged post-exercise soreness, lowered hormonal vitality, slow wound healing",
        therapeuticDose = "15 - 30 mg daily",
        primaryFoodSources = listOf("Fresh oysters", "Grass-fed beef", "Pumpkin seeds", "Hemp hearts", "Cashews"),
        synergyTip = "Avoid taking with high-phytate unsoaked grains; balance with 1mg copper if taking long term.",
        recommendedTiming = "With lunch or dinner (take with food to avoid stomach nausea)"
    ),
    OMEGA_3(
        id = "omega3",
        nutrientName = "Omega-3 (EPA & DHA Marine Lipids)",
        icon = "🐟",
        keySymptoms = "Joint creakiness, persistent muscle inflammation, dry skin, low seafood intake",
        therapeuticDose = "1,500 - 2,500 mg combined EPA + DHA",
        primaryFoodSources = listOf("Wild salmon", "Atlantic mackerel", "Sardines", "Algal EPA/DHA oil", "Chia seeds"),
        synergyTip = "Drastically down-regulates exercise-induced DOMS and promotes cellular membrane fluidity.",
        recommendedTiming = "With your highest-fat meal of the day"
    ),
    CALCIUM(
        id = "calcium",
        nutrientName = "Bioavailable Calcium",
        icon = "🦴",
        keySymptoms = "Frequent cramps, avoidance of dairy, brittle nails, muscle contraction weakness",
        therapeuticDose = "800 - 1,000 mg total daily",
        primaryFoodSources = listOf("Greek yogurt", "Aged parmesan", "Tahini (sesame)", "Kale", "Calcium-set tofu"),
        synergyTip = "Requires adequate Vitamin D3 and Magnesium for cellular influx into skeletal tissue.",
        recommendedTiming = "Divided morning and evening with meals"
    ),
    ELECTROLYTES(
        id = "electrolytes",
        nutrientName = "Electrolytes (Sodium, Potassium, Magnesium)",
        icon = "💧",
        keySymptoms = "Headaches, orthostatic lightheadedness, heavy sweating in workouts, keto fatigue",
        therapeuticDose = "3,500 mg Potassium, 3,000 mg Sodium, 400 mg Magnesium",
        primaryFoodSources = listOf("Coconut water", "Avocados", "Red skin potatoes", "Pink Himalayan rock salt"),
        synergyTip = "Sodium-potassium cellular pump is mandatory for glucose and amino acid transport into muscle cells.",
        recommendedTiming = "First thing in the morning and 30m prior to training"
    )
}

data class PlannedMeal(
    val mealType: String,
    val name: String,
    val description: String,
    val calories: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatsG: Int,
    val targetedDeficiencyBoosts: List<String>,
    val isProExclusive: Boolean = false
)

data class DeficiencyRemedyPlan(
    val deficiency: NutrientDeficiency,
    val urgencyScore: Int,
    val targetedDose: String,
    val lifestyleGuidance: String,
    val recommendedFoods: List<String>,
    val timingAdvice: String,
    val isProExclusive: Boolean = false
)

data class DietaryPlan(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val dietType: DietType,
    val goal: FitnessDietGoal,
    val mealFrequency: MealFrequency,
    val allergies: List<String>,
    val waterIntakeRating: String,
    val targetCalories: Int,
    val targetProtein: Int,
    val targetCarbs: Int,
    val targetFats: Int,
    val deficiencies: List<DeficiencyRemedyPlan>,
    val meals: List<PlannedMeal>,
    val dailyHabitsToAdd: List<String>,
    val isProUnlocked: Boolean = false
) {
    fun toJson(): String {
        val obj = org.json.JSONObject()
        obj.put("id", id)
        obj.put("timestamp", timestamp)
        obj.put("dietType", dietType.name)
        obj.put("goal", goal.name)
        obj.put("mealFrequency", mealFrequency.name)
        obj.put("allergies", org.json.JSONArray(allergies))
        obj.put("waterIntakeRating", waterIntakeRating)
        obj.put("targetCalories", targetCalories)
        obj.put("targetProtein", targetProtein)
        obj.put("targetCarbs", targetCarbs)
        obj.put("targetFats", targetFats)
        obj.put("dailyHabitsToAdd", org.json.JSONArray(dailyHabitsToAdd))
        obj.put("isProUnlocked", isProUnlocked)

        val defArr = org.json.JSONArray()
        deficiencies.forEach { d ->
            val dObj = org.json.JSONObject()
            dObj.put("deficiencyId", d.deficiency.id)
            dObj.put("urgencyScore", d.urgencyScore)
            dObj.put("targetedDose", d.targetedDose)
            dObj.put("lifestyleGuidance", d.lifestyleGuidance)
            dObj.put("recommendedFoods", org.json.JSONArray(d.recommendedFoods))
            dObj.put("timingAdvice", d.timingAdvice)
            dObj.put("isProExclusive", d.isProExclusive)
            defArr.put(dObj)
        }
        obj.put("deficiencies", defArr)

        val mealArr = org.json.JSONArray()
        meals.forEach { m ->
            val mObj = org.json.JSONObject()
            mObj.put("mealType", m.mealType)
            mObj.put("name", m.name)
            mObj.put("description", m.description)
            mObj.put("calories", m.calories)
            mObj.put("proteinG", m.proteinG)
            mObj.put("carbsG", m.carbsG)
            mObj.put("fatsG", m.fatsG)
            mObj.put("targetedDeficiencyBoosts", org.json.JSONArray(m.targetedDeficiencyBoosts))
            mObj.put("isProExclusive", m.isProExclusive)
            mealArr.put(mObj)
        }
        obj.put("meals", mealArr)

        return obj.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): DietaryPlan? {
            if (jsonStr.isBlank()) return null
            return try {
                val obj = org.json.JSONObject(jsonStr)
                val id = obj.optString("id", UUID.randomUUID().toString())
                val timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                val dietType = DietType.valueOf(obj.optString("dietType", DietType.OMNIVORE.name))
                val goal = FitnessDietGoal.valueOf(obj.optString("goal", FitnessDietGoal.LEAN_HYPERTROPHY.name))
                val mealFrequency = MealFrequency.valueOf(obj.optString("mealFrequency", MealFrequency.THREE_MEALS.name))
                val waterRating = obj.optString("waterIntakeRating", "2.5L / Optimal")
                val targetCalories = obj.optInt("targetCalories", 2400)
                val targetProtein = obj.optInt("targetProtein", 180)
                val targetCarbs = obj.optInt("targetCarbs", 250)
                val targetFats = obj.optInt("targetFats", 70)
                val isPro = obj.optBoolean("isProUnlocked", false)

                val allergiesList = mutableListOf<String>()
                val allergiesArr = obj.optJSONArray("allergies")
                if (allergiesArr != null) {
                    for (i in 0 until allergiesArr.length()) allergiesList.add(allergiesArr.getString(i))
                }

                val habitsList = mutableListOf<String>()
                val habitsArr = obj.optJSONArray("dailyHabitsToAdd")
                if (habitsArr != null) {
                    for (i in 0 until habitsArr.length()) habitsList.add(habitsArr.getString(i))
                }

                val deficienciesList = mutableListOf<DeficiencyRemedyPlan>()
                val defArr = obj.optJSONArray("deficiencies")
                if (defArr != null) {
                    for (i in 0 until defArr.length()) {
                        val dObj = defArr.getJSONObject(i)
                        val defId = dObj.optString("deficiencyId")
                        val defEnum = NutrientDeficiency.values().find { it.id == defId } ?: NutrientDeficiency.VITAMIN_D3
                        val foodsList = mutableListOf<String>()
                        val foodsArr = dObj.optJSONArray("recommendedFoods")
                        if (foodsArr != null) {
                            for (j in 0 until foodsArr.length()) foodsList.add(foodsArr.getString(j))
                        }
                        deficienciesList.add(
                            DeficiencyRemedyPlan(
                                deficiency = defEnum,
                                urgencyScore = dObj.optInt("urgencyScore", 8),
                                targetedDose = dObj.optString("targetedDose", defEnum.therapeuticDose),
                                lifestyleGuidance = dObj.optString("lifestyleGuidance", defEnum.synergyTip),
                                recommendedFoods = if (foodsList.isEmpty()) defEnum.primaryFoodSources else foodsList,
                                timingAdvice = dObj.optString("timingAdvice", defEnum.recommendedTiming),
                                isProExclusive = dObj.optBoolean("isProExclusive", false)
                            )
                        )
                    }
                }

                val mealsList = mutableListOf<PlannedMeal>()
                val mealArr = obj.optJSONArray("meals")
                if (mealArr != null) {
                    for (i in 0 until mealArr.length()) {
                        val mObj = mealArr.getJSONObject(i)
                        val boostsList = mutableListOf<String>()
                        val boostsArr = mObj.optJSONArray("targetedDeficiencyBoosts")
                        if (boostsArr != null) {
                            for (j in 0 until boostsArr.length()) boostsList.add(boostsArr.getString(j))
                        }
                        mealsList.add(
                            PlannedMeal(
                                mealType = mObj.optString("mealType", "Meal"),
                                name = mObj.optString("name", ""),
                                description = mObj.optString("description", ""),
                                calories = mObj.optInt("calories", 500),
                                proteinG = mObj.optInt("proteinG", 40),
                                carbsG = mObj.optInt("carbsG", 40),
                                fatsG = mObj.optInt("fatsG", 15),
                                targetedDeficiencyBoosts = boostsList,
                                isProExclusive = mObj.optBoolean("isProExclusive", false)
                            )
                        )
                    }
                }

                DietaryPlan(
                    id = id,
                    timestamp = timestamp,
                    dietType = dietType,
                    goal = goal,
                    mealFrequency = mealFrequency,
                    allergies = allergiesList,
                    waterIntakeRating = waterRating,
                    targetCalories = targetCalories,
                    targetProtein = targetProtein,
                    targetCarbs = targetCarbs,
                    targetFats = targetFats,
                    deficiencies = deficienciesList,
                    meals = mealsList,
                    dailyHabitsToAdd = habitsList,
                    isProUnlocked = isPro
                )
            } catch (_: Exception) {
                null
            }
        }
    }
}

object DietaryPlanGenerator {

    fun generatePlan(
        dietType: DietType,
        goal: FitnessDietGoal,
        mealFrequency: MealFrequency,
        allergies: List<String>,
        waterIntakeRating: String,
        selectedDeficiencies: List<NutrientDeficiency>,
        userWeightKg: Double = 77.0,
        isPro: Boolean = false
    ): DietaryPlan {
        // 1. Calculate Basal & Active Caloric Targets
        // Baseline maintenance roughly 33 kcal / kg for active fitness users
        val baselineMaintenance = (userWeightKg * 33).toInt().coerceIn(1800, 3200)
        val targetCalories = (baselineMaintenance + goal.calorieAdjustment).coerceIn(1500, 3800)

        // Protein calculation
        val targetProtein = (userWeightKg * goal.proteinPerKg).toInt().coerceIn(120, 240)

        // Carbs & Fats split according to dietType
        val proteinCalories = targetProtein * 4
        val remainingCalories = (targetCalories - proteinCalories).coerceAtLeast(600)

        val (targetCarbs, targetFats) = when (dietType) {
            DietType.KETOGENIC -> {
                val carbs = 30
                val fatCalories = remainingCalories - (carbs * 4)
                val fats = (fatCalories / 9).coerceAtLeast(80)
                Pair(carbs, fats)
            }
            DietType.LOW_CARB_PALEO -> {
                val carbCalories = (remainingCalories * 0.35).toInt()
                val fatCalories = remainingCalories - carbCalories
                Pair(carbCalories / 4, (fatCalories / 9).coerceAtLeast(60))
            }
            DietType.HIGH_PROTEIN_CARNIVORE -> {
                val carbs = 40
                val fatCalories = remainingCalories - (carbs * 4)
                val fats = (fatCalories / 9).coerceAtLeast(70)
                Pair(carbs, fats)
            }
            DietType.VEGAN, DietType.VEGETARIAN -> {
                val carbCalories = (remainingCalories * 0.65).toInt()
                val fatCalories = remainingCalories - carbCalories
                Pair(carbCalories / 4, (fatCalories / 9).coerceAtLeast(45))
            }
            else -> {
                // Omnivore / Mediterranean / Pescatarian balanced 50/50 split of remaining
                val carbCalories = (remainingCalories * 0.55).toInt()
                val fatCalories = remainingCalories - carbCalories
                Pair(carbCalories / 4, (fatCalories / 9).coerceAtLeast(55))
            }
        }

        // 2. Synthesize Deficiency Remediation Plans
        val deficienciesToProcess = if (selectedDeficiencies.isEmpty()) {
            listOf(NutrientDeficiency.VITAMIN_D3, NutrientDeficiency.MAGNESIUM)
        } else {
            selectedDeficiencies
        }

        val deficiencyRemedies = deficienciesToProcess.mapIndexed { index, def ->
            // In freemium tier, first deficiency is unlocked for free; remaining are Pro Exclusive
            val isProLocked = !isPro && index > 0
            val urgency = 10 - index.coerceAtMost(5)

            val customFoods = when (dietType) {
                DietType.VEGAN, DietType.VEGETARIAN -> {
                    when (def) {
                        NutrientDeficiency.IRON_FERRITIN -> listOf("Sprouted lentils", "Organic blackstrap molasses", "Hemp seeds", "Pumpkin seeds + lemon")
                        NutrientDeficiency.VITAMIN_B12 -> listOf("Fortified nutritional yeast (2 tbsp/day)", "Sublingual methylcobalamin 1000mcg", "Spirulina")
                        NutrientDeficiency.OMEGA_3 -> listOf("Algal DHA/EPA oil", "Fresh ground flaxseed", "Chia seeds", "Raw English walnuts")
                        NutrientDeficiency.ZINC -> listOf("Shelled hemp seeds", "Raw pumpkin seeds", "Cashews", "Lentil sprouts")
                        NutrientDeficiency.CALCIUM -> listOf("Calcium-set tofu", "Sesame tahini", "Collard greens", "Almond milk")
                        else -> def.primaryFoodSources
                    }
                }
                DietType.KETOGENIC -> {
                    when (def) {
                        NutrientDeficiency.ELECTROLYTES -> listOf("Celtic sea salt in water", "Whole Haas avocado", "Bone broth with sodium", "Spinach sautéed in ghee")
                        NutrientDeficiency.MAGNESIUM -> listOf("Pumpkin seeds", "Dark chocolate 90%", "Avocado", "Electrolyte magnesium powder")
                        else -> def.primaryFoodSources
                    }
                }
                else -> def.primaryFoodSources
            }

            DeficiencyRemedyPlan(
                deficiency = def,
                urgencyScore = urgency,
                targetedDose = def.therapeuticDose,
                lifestyleGuidance = def.synergyTip,
                recommendedFoods = customFoods,
                timingAdvice = def.recommendedTiming,
                isProExclusive = isProLocked
            )
        }

        // 3. Synthesize Curated Meals for their specific diet
        val meals = generateMealsForDiet(dietType, targetCalories, targetProtein, targetCarbs, targetFats, deficienciesToProcess, isPro)

        // 4. Generate Habit Triggers for Daily Routines
        val dailyHabits = mutableListOf<String>()
        deficienciesToProcess.take(3).forEach { def ->
            when (def) {
                NutrientDeficiency.VITAMIN_D3 -> dailyHabits.add("Take 2,000-4,000 IU Vit D3 + K2 with breakfast fats")
                NutrientDeficiency.MAGNESIUM -> dailyHabits.add("Take 400mg Magnesium Glycinate 45m before sleep")
                NutrientDeficiency.IRON_FERRITIN -> dailyHabits.add("Consume iron-dense food with Vitamin C (avoid caffeine 2hr)")
                NutrientDeficiency.VITAMIN_B12 -> dailyHabits.add("Take sublingual Methyl-B12 with morning hydration")
                NutrientDeficiency.OMEGA_3 -> dailyHabits.add("Consume 2g EPA/DHA marine omega-3 with largest meal")
                NutrientDeficiency.ELECTROLYTES -> dailyHabits.add("Morning Electrolyte Surge: 500ml water + 1/2 tsp pink salt")
                NutrientDeficiency.ZINC -> dailyHabits.add("Take 25mg Zinc Bisglycinate with evening dinner")
                NutrientDeficiency.CALCIUM -> dailyHabits.add("Hit 1,000mg bioavailable calcium target via whole foods")
            }
        }
        if (dailyHabits.size < 2) {
            dailyHabits.add("Drink 2.5L structured water with electrolyte minerals")
        }

        return DietaryPlan(
            dietType = dietType,
            goal = goal,
            mealFrequency = mealFrequency,
            allergies = allergies,
            waterIntakeRating = waterIntakeRating,
            targetCalories = targetCalories,
            targetProtein = targetProtein,
            targetCarbs = targetCarbs,
            targetFats = targetFats,
            deficiencies = deficiencyRemedyPlanList(deficiencyRemedies),
            meals = meals,
            dailyHabitsToAdd = dailyHabits,
            isProUnlocked = isPro
        )
    }

    private fun deficiencyRemedyPlanList(list: List<DeficiencyRemedyPlan>): List<DeficiencyRemedyPlan> = list

    private fun generateMealsForDiet(
        dietType: DietType,
        cal: Int,
        p: Int,
        c: Int,
        f: Int,
        deficiencies: List<NutrientDeficiency>,
        isPro: Boolean
    ): List<PlannedMeal> {
        val defNames = deficiencies.map { it.nutrientName.split(" ").first() }

        val breakfast: PlannedMeal
        val lunch: PlannedMeal
        val dinner: PlannedMeal
        val snack: PlannedMeal

        when (dietType) {
            DietType.KETOGENIC -> {
                breakfast = PlannedMeal(
                    mealType = "Breakfast",
                    name = "Keto Pastured Eggs & Avocado Bowl",
                    description = "3 pastured eggs scrambled in grass-fed ghee, 1 whole sliced avocado, wilted spinach & Celtic salt.",
                    calories = (cal * 0.28).toInt(),
                    proteinG = (p * 0.25).toInt(),
                    carbsG = 6,
                    fatsG = (f * 0.32).toInt(),
                    targetedDeficiencyBoosts = listOf("Electrolytes (Potassium)", "Vitamin D3", "Magnesium"),
                    isProExclusive = false
                )
                lunch = PlannedMeal(
                    mealType = "Lunch",
                    name = "Wild Salmon & Olive Green Power Plate",
                    description = "200g wild pan-seared salmon fillet over mixed arugula, cucumber, kalamata olives, hemp hearts & extra virgin olive oil.",
                    calories = (cal * 0.35).toInt(),
                    proteinG = (p * 0.35).toInt(),
                    carbsG = 8,
                    fatsG = (f * 0.38).toInt(),
                    targetedDeficiencyBoosts = listOf("Omega-3 (EPA/DHA)", "Vitamin D3", "Magnesium"),
                    isProExclusive = false
                )
                dinner = PlannedMeal(
                    mealType = "Dinner",
                    name = "Prime Ribeye & Roasted Asparagus in Herb Butter",
                    description = "250g grilled grass-fed ribeye steak served with tender roasted asparagus spears tossed in garlic grass-fed butter.",
                    calories = (cal * 0.30).toInt(),
                    proteinG = (p * 0.32).toInt(),
                    carbsG = 5,
                    fatsG = (f * 0.30).toInt(),
                    targetedDeficiencyBoosts = listOf("Zinc", "Iron & Ferritin", "Vitamin B12"),
                    isProExclusive = !isPro
                )
                snack = PlannedMeal(
                    mealType = "Recovery Fuel",
                    name = "Electrolyte Macadamia & Dark Cacao Crunch",
                    description = "35g raw macadamia nuts with 2 squares 90% dark chocolate and a pinch of pink salt.",
                    calories = (cal * 0.07).toInt(),
                    proteinG = (p * 0.08).toInt(),
                    carbsG = 4,
                    fatsG = (f * 0.10).toInt(),
                    targetedDeficiencyBoosts = listOf("Magnesium", "Electrolytes"),
                    isProExclusive = !isPro
                )
            }
            DietType.VEGAN -> {
                breakfast = PlannedMeal(
                    mealType = "Breakfast",
                    name = "Superseed High-Protein Sprouted Oats & Berries",
                    description = "Organic gluten-free oats cooked with fortified almond milk, pea/rice protein isolate, 2 tbsp chia seeds, pumpkin seeds and wild blueberries.",
                    calories = (cal * 0.28).toInt(),
                    proteinG = (p * 0.26).toInt(),
                    carbsG = (c * 0.32).toInt(),
                    fatsG = (f * 0.25).toInt(),
                    targetedDeficiencyBoosts = listOf("Iron", "Magnesium", "Zinc"),
                    isProExclusive = false
                )
                lunch = PlannedMeal(
                    mealType = "Lunch",
                    name = "Crispy Organic Tempeh & Quinoa Rainbow Bowl",
                    description = "180g pan-crisped organic tempeh, 1 cup tri-color quinoa, steamed broccoli, edamame and lemon-tahini dressing.",
                    calories = (cal * 0.35).toInt(),
                    proteinG = (p * 0.34).toInt(),
                    carbsG = (c * 0.36).toInt(),
                    fatsG = (f * 0.35).toInt(),
                    targetedDeficiencyBoosts = listOf("Calcium", "Iron", "Zinc"),
                    isProExclusive = false
                )
                dinner = PlannedMeal(
                    mealType = "Dinner",
                    name = "Rich Lentil Dahl with Nutritional Yeast & Spinach",
                    description = "Spiced black Beluga lentils simmered with turmeric, ginger, tomatoes, heaps of baby spinach, topped with 2 tbsp fortified nutritional yeast.",
                    calories = (cal * 0.27).toInt(),
                    proteinG = (p * 0.28).toInt(),
                    carbsG = (c * 0.24).toInt(),
                    fatsG = (f * 0.25).toInt(),
                    targetedDeficiencyBoosts = listOf("Vitamin B12", "Iron & Ferritin", "Magnesium"),
                    isProExclusive = !isPro
                )
                snack = PlannedMeal(
                    mealType = "Recovery Fuel",
                    name = "Spirulina Algal Omega-3 Recovery Shake",
                    description = "Plant protein shake with Hawaiian spirulina, ground flaxseed and coconut water for electrolyte replenishment.",
                    calories = (cal * 0.10).toInt(),
                    proteinG = (p * 0.12).toInt(),
                    carbsG = (c * 0.08).toInt(),
                    fatsG = (f * 0.15).toInt(),
                    targetedDeficiencyBoosts = listOf("Omega-3", "Electrolytes", "B12"),
                    isProExclusive = !isPro
                )
            }
            DietType.MEDITERRANEAN -> {
                breakfast = PlannedMeal(
                    mealType = "Breakfast",
                    name = "Greek Yogurt Power Parfait with Walnuts & Honey",
                    description = "250g authentic Greek yogurt (0% or 2%), crushed English walnuts, raw pumpkin seeds, cinnamon and fresh pomegranate seeds.",
                    calories = (cal * 0.28).toInt(),
                    proteinG = (p * 0.28).toInt(),
                    carbsG = (c * 0.22).toInt(),
                    fatsG = (f * 0.28).toInt(),
                    targetedDeficiencyBoosts = listOf("Calcium", "Omega-3", "Zinc"),
                    isProExclusive = false
                )
                lunch = PlannedMeal(
                    mealType = "Lunch",
                    name = "Mediterranean Grilled Chicken & Olive Quinoa Salad",
                    description = "200g grilled herb chicken breast, quinoa, diced cucumbers, cherry tomatoes, kalamata olives, feta cheese and cold-pressed olive oil.",
                    calories = (cal * 0.35).toInt(),
                    proteinG = (p * 0.35).toInt(),
                    carbsG = (c * 0.35).toInt(),
                    fatsG = (f * 0.35).toInt(),
                    targetedDeficiencyBoosts = listOf("Zinc", "Vitamin D3", "Magnesium"),
                    isProExclusive = false
                )
                dinner = PlannedMeal(
                    mealType = "Dinner",
                    name = "Pan-Seared Sea Bass with Lemon Roasted Potatoes & Greens",
                    description = "220g wild Mediterranean sea bass fillet, roasted garlic baby potatoes, steamed Swiss chard with a generous squeeze of fresh lemon.",
                    calories = (cal * 0.29).toInt(),
                    proteinG = (p * 0.30).toInt(),
                    carbsG = (c * 0.32).toInt(),
                    fatsG = (f * 0.27).toInt(),
                    targetedDeficiencyBoosts = listOf("Omega-3", "Electrolytes", "Vitamin D3"),
                    isProExclusive = !isPro
                )
                snack = PlannedMeal(
                    mealType = "Recovery Fuel",
                    name = "Spiced Roasted Chickpeas & Dark Chocolate",
                    description = "Handful of crunchy cumin chickpeas paired with 25g 85% dark chocolate.",
                    calories = (cal * 0.08).toInt(),
                    proteinG = (p * 0.07).toInt(),
                    carbsG = (c * 0.11).toInt(),
                    fatsG = (f * 0.10).toInt(),
                    targetedDeficiencyBoosts = listOf("Magnesium", "Iron"),
                    isProExclusive = !isPro
                )
            }
            else -> {
                // Standard Omnivore / Carnivore / Pescatarian / Paleo
                breakfast = PlannedMeal(
                    mealType = "Breakfast",
                    name = "Power Fuel Egg Scramble & Sprouted Toast",
                    description = "3 pastured eggs + 100g egg whites scrambled with spinach and bell peppers, 1 slice sprouted grain toast, 1/2 avocado.",
                    calories = (cal * 0.28).toInt(),
                    proteinG = (p * 0.28).toInt(),
                    carbsG = (c * 0.24).toInt(),
                    fatsG = (f * 0.30).toInt(),
                    targetedDeficiencyBoosts = listOf("Vitamin D3", "Choline", "Electrolytes"),
                    isProExclusive = false
                )
                lunch = PlannedMeal(
                    mealType = "Lunch",
                    name = "Flame-Grilled Chicken Breast, Sweet Potato & Broccoli",
                    description = "220g tender grilled chicken breast, 200g baked cinnamon sweet potato, 150g steamed broccoli florets with 1 tbsp olive oil.",
                    calories = (cal * 0.35).toInt(),
                    proteinG = (p * 0.38).toInt(),
                    carbsG = (c * 0.38).toInt(),
                    fatsG = (f * 0.30).toInt(),
                    targetedDeficiencyBoosts = listOf("Potassium", "Zinc", "Magnesium"),
                    isProExclusive = false
                )
                dinner = PlannedMeal(
                    mealType = "Dinner",
                    name = "Wild Sockeye Salmon or Lean Sirloin with Asparagus",
                    description = "220g wild sockeye salmon or grass-fed sirloin, jasmine rice with coconut water, grilled asparagus and lemon butter.",
                    calories = (cal * 0.28).toInt(),
                    proteinG = (p * 0.28).toInt(),
                    carbsG = (c * 0.28).toInt(),
                    fatsG = (f * 0.30).toInt(),
                    targetedDeficiencyBoosts = listOf("Omega-3", "Iron & Ferritin", "Vitamin B12"),
                    isProExclusive = !isPro
                )
                snack = PlannedMeal(
                    mealType = "Recovery Fuel",
                    name = "Whey Isolate Protein Shake & Raw Pumpkin Seeds",
                    description = "1 scoop cold-filtered whey isolate, 30g raw pumpkin seeds, and 1 medium banana.",
                    calories = (cal * 0.09).toInt(),
                    proteinG = (p * 0.06).toInt(),
                    carbsG = (c * 0.10).toInt(),
                    fatsG = (f * 0.10).toInt(),
                    targetedDeficiencyBoosts = listOf("Zinc", "Magnesium", "Electrolytes"),
                    isProExclusive = !isPro
                )
            }
        }

        return listOf(breakfast, lunch, dinner, snack)
    }
}
