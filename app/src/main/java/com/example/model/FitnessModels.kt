package com.example.model

data class UserProfile(
    val level: Int = 1,
    val xp: Int = 0,
    val streakDays: Int = 1,
    val streakFreezes: Int = 1,
    val lastActiveDate: String = "",
    val totalVolumeKg: Double = 0.0,
    val totalWorkoutsLogged: Int = 0,
    val totalMealsLogged: Int = 0,
    val unlockedBadgeIds: List<String> = emptyList(),
    // RPG Attributes System
    val strength: Int = 10,     // Built by Push/Pull/Legs volume
    val agility: Int = 10,      // Built by Cardio / Steps / High-rep endurance
    val endurance: Int = 10,    // Built by consistent calorie and macro logging
    // Cosmetics & Customization
    val purchasedItemIds: List<String> = listOf("skin_neon_striker", "wp_cyber_grid"),
    val activeSkinId: String = "skin_neon_striker",
    val activeWallpaperId: String = "wp_cyber_grid",
    // Freemium Pro Gate
    val isProUnlocked: Boolean = false
) {
    val levelTitle: String
        get() = when (level) {
            1 -> "Cyber Recruit"
            2 -> "Neon Striker"
            3 -> "Synapse Lifter"
            4 -> "Overload Specialist"
            5 -> "Quantum Athlete"
            6 -> "Titan of Chrome"
            else -> "Apex Cyber Lord (Lvl $level)"
        }
}

enum class MuscleGroup(val displayName: String, val exercises: List<String>) {
    CHEST("Chest", listOf("Flat Barbell Bench", "Incline Dumbbell Press", "Cable Chest Fly", "Dips")),
    BACK("Back", listOf("Deadlift", "Barbell Row", "Weighted Pull-up", "Lat Pulldown")),
    LEGS("Legs", listOf("Barbell Back Squat", "Romanian Deadlift", "Leg Press", "Bulgarian Split Squat")),
    ARMS("Arms", listOf("Barbell Bicep Curl", "Skull Crushers", "Hammer Curls", "Tricep Pushdown")),
    SHOULDERS("Shoulders", listOf("Overhead Press", "Dumbbell Lateral Raise", "Face Pulls", "Arnold Press")),
    CORE("Core", listOf("Hanging Leg Raise", "Ab Wheel Rollout", "Plank Hold", "Cable Crunch")),
    CARDIO("Cardio", listOf("Treadmill Sprints", "Rowing Intervals", "Jump Rope", "Stair Climber"))
}

data class WorkoutLog(
    val id: String,
    val exerciseName: String,
    val muscleGroup: String,
    val weightKg: Double,
    val sets: Int,
    val reps: Int,
    val totalVolumeKg: Double,
    val timestamp: Long
)

data class NutritionLog(
    val id: String,
    val mealName: String,
    val calories: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatsG: Int,
    val timestamp: Long,
    val servingSizeG: Double = 100.0,
    val fiberG: Double = 0.0,
    val sugarG: Double = 0.0,
    val sodiumMg: Double = 0.0,
    val potassiumMg: Double = 0.0,
    val calciumMg: Double = 0.0,
    val ironMg: Double = 0.0,
    val vitaminCIu: Double = 0.0,
    val vitaminDIu: Double = 0.0,
    val magnesiumMg: Double = 0.0,
    val zincMg: Double = 0.0,
    val barcode: String = ""
)

data class NutritionGoals(
    val targetCalories: Int = 2400,
    val targetProtein: Int = 180,
    val targetCarbs: Int = 250,
    val targetFats: Int = 70
)

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val xpReward: Int = 100
)

val APP_BADGES = listOf(
    Badge("first_blood", "First Blood", "Log your first workout session", "⚡", 50),
    Badge("iron_will", "Iron Will", "Reach a 7-day active workout streak", "🔥", 150),
    Badge("volume_behemoth", "Volume Behemoth", "Surpass 10,000 kg total cumulative workout volume", "🦾", 200),
    Badge("fuel_master", "Fuel Master", "Log 5 meals meeting daily protein targets", "🧪", 100),
    Badge("golem_slayer", "Golem Slayer", "Defeat the weekly Bench Press Golem boss", "⚔️", 300),
    Badge("cyber_titan", "Cyber Titan", "Surpass Level 5 and transcend human limits", "👑", 250)
)

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPending: Boolean = false
)

// Daily Routine Task Model
data class DailyRoutine(
    val id: String,
    val title: String,
    val category: String, // "WORKOUT", "NUTRITION", "RECOVERY", "MINDSET"
    val xpReward: Int,
    val attributeType: String, // "STR", "AGI", "END"
    val attributeReward: Int,
    val isCompleted: Boolean = false
)

// Weekly Boss Quest Model
data class BossQuest(
    val id: String,
    val title: String,
    val bossName: String,
    val description: String,
    val targetVolumeKg: Double,
    val currentVolumeKg: Double,
    val xpReward: Int,
    val attributeType: String,
    val attributeReward: Int,
    val isDefeated: Boolean = false,
    val expiresAt: Long
) {
    val progressFraction: Float
        get() = (currentVolumeKg / targetVolumeKg).toFloat().coerceIn(0f, 1f)
}

// XP Store Cosmetics (Skins & Wallpapers)
enum class ShopItemType {
    SKIN, WALLPAPER
}

data class ShopItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: ShopItemType,
    val costXp: Int,
    val previewHex: Long,
    val accentHex: Long,
    val iconEmoji: String,
    val isProExclusive: Boolean = false
)

val SHOP_ITEMS = listOf(
    ShopItem(
        id = "skin_neon_striker",
        title = "Neon Striker (Default)",
        subtitle = "Classic electric cyan & magenta HUD",
        type = ShopItemType.SKIN,
        costXp = 0,
        previewHex = 0xFF00F0FF,
        accentHex = 0xFFFF007F,
        iconEmoji = "⚡"
    ),
    ShopItem(
        id = "skin_cyber_gold",
        title = "Cyber Gold Titan",
        subtitle = "Prestige gold & amber matrix",
        type = ShopItemType.SKIN,
        costXp = 250,
        previewHex = 0xFFFFB800,
        accentHex = 0xFFFF4500,
        iconEmoji = "👑"
    ),
    ShopItem(
        id = "skin_toxic_biohazard",
        title = "Biohazard Toxic",
        subtitle = "Radioactive green & neon lime",
        type = ShopItemType.SKIN,
        costXp = 350,
        previewHex = 0xFF39FF14,
        accentHex = 0xFF00FFCC,
        iconEmoji = "☣️"
    ),
    ShopItem(
        id = "skin_plasma_purple",
        title = "Plasma Void",
        subtitle = "Deep nebula violet & UV glow",
        type = ShopItemType.SKIN,
        costXp = 500,
        previewHex = 0xFF9D00FF,
        accentHex = 0xFFFF00E5,
        iconEmoji = "🔮"
    ),
    ShopItem(
        id = "wp_cyber_grid",
        title = "Cyber Grid",
        subtitle = "Minimalist dark high-density synth grid",
        type = ShopItemType.WALLPAPER,
        costXp = 0,
        previewHex = 0xFF11131D,
        accentHex = 0xFF252A3D,
        iconEmoji = "🌐"
    ),
    ShopItem(
        id = "wp_neon_matrix",
        title = "Digital Matrix Rain",
        subtitle = "Subtle digital data stream backdrop",
        type = ShopItemType.WALLPAPER,
        costXp = 200,
        previewHex = 0xFF0A140F,
        accentHex = 0xFF00FF66,
        iconEmoji = "💻"
    ),
    ShopItem(
        id = "wp_solar_forge",
        title = "Solar Iron Forge",
        subtitle = "Molten crimson & ember workout arena",
        type = ShopItemType.WALLPAPER,
        costXp = 400,
        previewHex = 0xFF1C0A0A,
        accentHex = 0xFFFF3300,
        iconEmoji = "🔥"
    ),
    ShopItem(
        id = "wp_quantum_nexus",
        title = "Quantum Nexus Pro",
        subtitle = "Dynamic holographic crystalline weave",
        type = ShopItemType.WALLPAPER,
        costXp = 600,
        previewHex = 0xFF05101A,
        accentHex = 0xFF00E5FF,
        iconEmoji = "💎",
        isProExclusive = true
    )
)

data class WeightLog(
    val id: String,
    val weightKg: Double,
    val note: String = "",
    val timestamp: Long
)

data class DailyTrendPoint(
    val dateLabel: String,
    val dayNumber: Int,
    val timestamp: Long,
    val weightKg: Double?,
    val workoutVolumeKg: Double,
    val hasWorkout: Boolean
)

