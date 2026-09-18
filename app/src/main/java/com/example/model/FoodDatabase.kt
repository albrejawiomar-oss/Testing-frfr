package com.example.model

/**
 * Standard food database definition containing per-100g nutritional profile
 * including calories, macros, and full micronutrient breakdown (vitamins & minerals).
 */
data class FoodItem(
    val id: String,
    val name: String,
    val category: String,
    val barcode: String,
    val defaultServingG: Double = 100.0,
    val servingUnit: String = "g",
    // Base nutrients per 100g
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatsPer100g: Double,
    // Detailed Micronutrients per 100g
    val fiberPer100g: Double = 0.0,
    val sugarPer100g: Double = 0.0,
    val sodiumMgPer100g: Double = 0.0,
    val potassiumMgPer100g: Double = 0.0,
    val calciumMgPer100g: Double = 0.0,
    val ironMgPer100g: Double = 0.0,
    val vitaminCIuPer100g: Double = 0.0,
    val vitaminDIuPer100g: Double = 0.0,
    val magnesiumMgPer100g: Double = 0.0,
    val zincMgPer100g: Double = 0.0,
    val iconEmoji: String = "🥗"
) {
    /**
     * Compute exact calculated nutrient totals given an arbitrary serving size in grams.
     */
    fun calculateForServing(servingG: Double): CalculatedNutrients {
        val factor = (servingG / 100.0).coerceAtLeast(0.0)
        return CalculatedNutrients(
            servingSizeG = servingG,
            calories = (caloriesPer100g * factor).toInt(),
            proteinG = (proteinPer100g * factor).toInt(),
            carbsG = (carbsPer100g * factor).toInt(),
            fatsG = (fatsPer100g * factor).toInt(),
            fiberG = round1(fiberPer100g * factor),
            sugarG = round1(sugarPer100g * factor),
            sodiumMg = round1(sodiumMgPer100g * factor),
            potassiumMg = round1(potassiumMgPer100g * factor),
            calciumMg = round1(calciumMgPer100g * factor),
            ironMg = round1(ironMgPer100g * factor),
            vitaminCIu = round1(vitaminCIuPer100g * factor),
            vitaminDIu = round1(vitaminDIuPer100g * factor),
            magnesiumMg = round1(magnesiumMgPer100g * factor),
            zincMg = round1(zincMgPer100g * factor)
        )
    }

    private fun round1(value: Double): Double = Math.round(value * 10.0) / 10.0
}

data class CalculatedNutrients(
    val servingSizeG: Double,
    val calories: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatsG: Int,
    val fiberG: Double,
    val sugarG: Double,
    val sodiumMg: Double,
    val potassiumMg: Double,
    val calciumMg: Double,
    val ironMg: Double,
    val vitaminCIu: Double,
    val vitaminDIu: Double,
    val magnesiumMg: Double,
    val zincMg: Double
)

/**
 * Standard food catalog with popular everyday fitness foods, gym staples, and real barcode values.
 */
object FoodDatabase {
    val FOOD_CATALOG: List<FoodItem> = listOf(
        FoodItem(
            id = "food_chicken_breast",
            name = "Skinless Boneless Chicken Breast",
            category = "Poultry & Meat",
            barcode = "012345678905",
            defaultServingG = 150.0,
            caloriesPer100g = 165.0,
            proteinPer100g = 31.0,
            carbsPer100g = 0.0,
            fatsPer100g = 3.6,
            fiberPer100g = 0.0,
            sugarPer100g = 0.0,
            sodiumMgPer100g = 74.0,
            potassiumMgPer100g = 256.0,
            calciumMgPer100g = 15.0,
            ironMgPer100g = 1.0,
            vitaminCIuPer100g = 0.0,
            vitaminDIuPer100g = 5.0,
            magnesiumMgPer100g = 29.0,
            zincMgPer100g = 1.0,
            iconEmoji = "🍗"
        ),
        FoodItem(
            id = "food_whey_protein",
            name = "Optimum Gold Standard Whey Protein",
            category = "Supplements",
            barcode = "748927028669",
            defaultServingG = 30.0,
            caloriesPer100g = 400.0,
            proteinPer100g = 80.0,
            carbsPer100g = 10.0,
            fatsPer100g = 4.0,
            fiberPer100g = 1.0,
            sugarPer100g = 4.0,
            sodiumMgPer100g = 430.0,
            potassiumMgPer100g = 730.0,
            calciumMgPer100g = 430.0,
            ironMgPer100g = 2.4,
            vitaminCIuPer100g = 0.0,
            vitaminDIuPer100g = 0.0,
            magnesiumMgPer100g = 85.0,
            zincMgPer100g = 2.5,
            iconEmoji = "🥤"
        ),
        FoodItem(
            id = "food_white_rice",
            name = "Jasmine White Rice (Cooked)",
            category = "Grains",
            barcode = "070038593412",
            defaultServingG = 180.0,
            caloriesPer100g = 130.0,
            proteinPer100g = 2.7,
            carbsPer100g = 28.2,
            fatsPer100g = 0.3,
            fiberPer100g = 0.4,
            sugarPer100g = 0.1,
            sodiumMgPer100g = 1.0,
            potassiumMgPer100g = 35.0,
            calciumMgPer100g = 10.0,
            ironMgPer100g = 1.2,
            vitaminCIuPer100g = 0.0,
            vitaminDIuPer100g = 0.0,
            magnesiumMgPer100g = 12.0,
            zincMgPer100g = 0.8,
            iconEmoji = "🍚"
        ),
        FoodItem(
            id = "food_eggs_whole",
            name = "Whole Grade A Large Eggs",
            category = "Dairy & Eggs",
            barcode = "011110416008",
            defaultServingG = 100.0, // approx 2 large eggs
            caloriesPer100g = 143.0,
            proteinPer100g = 12.6,
            carbsPer100g = 0.7,
            fatsPer100g = 9.5,
            fiberPer100g = 0.0,
            sugarPer100g = 0.4,
            sodiumMgPer100g = 142.0,
            potassiumMgPer100g = 138.0,
            calciumMgPer100g = 56.0,
            ironMgPer100g = 1.8,
            vitaminCIuPer100g = 0.0,
            vitaminDIuPer100g = 82.0,
            magnesiumMgPer100g = 12.0,
            zincMgPer100g = 1.3,
            iconEmoji = "🥚"
        ),
        FoodItem(
            id = "food_rolled_oats",
            name = "Quaker Whole Grain Rolled Oats",
            category = "Grains",
            barcode = "030000010402",
            defaultServingG = 60.0,
            caloriesPer100g = 389.0,
            proteinPer100g = 16.9,
            carbsPer100g = 66.3,
            fatsPer100g = 6.9,
            fiberPer100g = 10.6,
            sugarPer100g = 1.0,
            sodiumMgPer100g = 2.0,
            potassiumMgPer100g = 429.0,
            calciumMgPer100g = 54.0,
            ironMgPer100g = 4.7,
            vitaminCIuPer100g = 0.0,
            vitaminDIuPer100g = 0.0,
            magnesiumMgPer100g = 177.0,
            zincMgPer100g = 4.0,
            iconEmoji = "🥣"
        ),
        FoodItem(
            id = "food_banana",
            name = "Fresh Cavendish Banana",
            category = "Fruits",
            barcode = "000000004011",
            defaultServingG = 118.0,
            caloriesPer100g = 89.0,
            proteinPer100g = 1.1,
            carbsPer100g = 22.8,
            fatsPer100g = 0.3,
            fiberPer100g = 2.6,
            sugarPer100g = 12.2,
            sodiumMgPer100g = 1.0,
            potassiumMgPer100g = 358.0,
            calciumMgPer100g = 5.0,
            ironMgPer100g = 0.3,
            vitaminCIuPer100g = 8.7,
            vitaminDIuPer100g = 0.0,
            magnesiumMgPer100g = 27.0,
            zincMgPer100g = 0.2,
            iconEmoji = "🍌"
        ),
        FoodItem(
            id = "food_greek_yogurt",
            name = "Chobani Non-Fat Plain Greek Yogurt",
            category = "Dairy & Eggs",
            barcode = "894700010045",
            defaultServingG = 170.0,
            caloriesPer100g = 59.0,
            proteinPer100g = 10.0,
            carbsPer100g = 3.6,
            fatsPer100g = 0.4,
            fiberPer100g = 0.0,
            sugarPer100g = 3.2,
            sodiumMgPer100g = 36.0,
            potassiumMgPer100g = 141.0,
            calciumMgPer100g = 110.0,
            ironMgPer100g = 0.1,
            vitaminCIuPer100g = 0.0,
            vitaminDIuPer100g = 5.0,
            magnesiumMgPer100g = 11.0,
            zincMgPer100g = 0.5,
            iconEmoji = "🥛"
        ),
        FoodItem(
            id = "food_atlantic_salmon",
            name = "Wild Atlantic Salmon Fillet",
            category = "Seafood",
            barcode = "021234567891",
            defaultServingG = 150.0,
            caloriesPer100g = 208.0,
            proteinPer100g = 20.4,
            carbsPer100g = 0.0,
            fatsPer100g = 13.4,
            fiberPer100g = 0.0,
            sugarPer100g = 0.0,
            sodiumMgPer100g = 59.0,
            potassiumMgPer100g = 363.0,
            calciumMgPer100g = 9.0,
            ironMgPer100g = 0.3,
            vitaminCIuPer100g = 3.9,
            vitaminDIuPer100g = 526.0,
            magnesiumMgPer100g = 27.0,
            zincMgPer100g = 0.4,
            iconEmoji = "🐟"
        ),
        FoodItem(
            id = "food_avocado",
            name = "Fresh Hass Avocado",
            category = "Fruits & Fats",
            barcode = "000000004225",
            defaultServingG = 100.0,
            caloriesPer100g = 160.0,
            proteinPer100g = 2.0,
            carbsPer100g = 8.5,
            fatsPer100g = 14.7,
            fiberPer100g = 6.7,
            sugarPer100g = 0.7,
            sodiumMgPer100g = 7.0,
            potassiumMgPer100g = 485.0,
            calciumMgPer100g = 12.0,
            ironMgPer100g = 0.6,
            vitaminCIuPer100g = 10.0,
            vitaminDIuPer100g = 0.0,
            magnesiumMgPer100g = 29.0,
            zincMgPer100g = 0.6,
            iconEmoji = "🥑"
        ),
        FoodItem(
            id = "food_almonds",
            name = "Raw Unsalted Whole Almonds",
            category = "Nuts & Seeds",
            barcode = "041220790123",
            defaultServingG = 30.0,
            caloriesPer100g = 579.0,
            proteinPer100g = 21.2,
            carbsPer100g = 21.6,
            fatsPer100g = 49.9,
            fiberPer100g = 12.5,
            sugarPer100g = 4.4,
            sodiumMgPer100g = 1.0,
            potassiumMgPer100g = 733.0,
            calciumMgPer100g = 269.0,
            ironMgPer100g = 3.7,
            vitaminCIuPer100g = 0.0,
            vitaminDIuPer100g = 0.0,
            magnesiumMgPer100g = 270.0,
            zincMgPer100g = 3.1,
            iconEmoji = "🥜"
        ),
        FoodItem(
            id = "food_peanut_butter",
            name = "Skippy Creamy Peanut Butter",
            category = "Spreads & Fats",
            barcode = "037600105047",
            defaultServingG = 32.0,
            caloriesPer100g = 588.0,
            proteinPer100g = 25.0,
            carbsPer100g = 20.0,
            fatsPer100g = 50.0,
            fiberPer100g = 6.0,
            sugarPer100g = 9.0,
            sodiumMgPer100g = 429.0,
            potassiumMgPer100g = 649.0,
            calciumMgPer100g = 43.0,
            ironMgPer100g = 1.9,
            vitaminCIuPer100g = 0.0,
            vitaminDIuPer100g = 0.0,
            magnesiumMgPer100g = 154.0,
            zincMgPer100g = 2.9,
            iconEmoji = "🥜"
        ),
        FoodItem(
            id = "food_broccoli",
            name = "Steamed Green Broccoli Florets",
            category = "Vegetables",
            barcode = "000000004060",
            defaultServingG = 150.0,
            caloriesPer100g = 35.0,
            proteinPer100g = 2.4,
            carbsPer100g = 7.2,
            fatsPer100g = 0.4,
            fiberPer100g = 2.6,
            sugarPer100g = 1.4,
            sodiumMgPer100g = 41.0,
            potassiumMgPer100g = 293.0,
            calciumMgPer100g = 40.0,
            ironMgPer100g = 0.7,
            vitaminCIuPer100g = 89.2,
            vitaminDIuPer100g = 0.0,
            magnesiumMgPer100g = 21.0,
            zincMgPer100g = 0.4,
            iconEmoji = "🥦"
        )
    )

    fun findByBarcode(barcode: String): FoodItem? {
        val clean = barcode.trim().filter { it.isDigit() }
        if (clean.isBlank()) return null
        return FOOD_CATALOG.find { it.barcode == clean || it.barcode.endsWith(clean) || clean.endsWith(it.barcode) }
    }

    fun search(query: String): List<FoodItem> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return FOOD_CATALOG
        return FOOD_CATALOG.filter {
            it.name.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.barcode.contains(q)
        }
    }
}
