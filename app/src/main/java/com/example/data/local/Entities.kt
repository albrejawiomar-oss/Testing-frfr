package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutEntity(
    @PrimaryKey val id: String,
    val exerciseName: String,
    val muscleGroup: String,
    val weightKg: Double,
    val sets: Int,
    val reps: Int,
    val totalVolumeKg: Double,
    val timestamp: Long
)

@Entity(tableName = "nutrition_logs")
data class NutritionEntity(
    @PrimaryKey val id: String,
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

@Entity(tableName = "daily_routines")
data class DailyRoutineEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "WORKOUT", "NUTRITION", "RECOVERY", "MINDSET"
    val xpReward: Int,
    val attributeType: String, // "STR", "AGI", "END"
    val attributeReward: Int,
    val isCompleted: Boolean = false,
    val dateString: String
)

@Entity(tableName = "boss_quests")
data class BossQuestEntity(
    @PrimaryKey val id: String,
    val title: String,
    val bossName: String,
    val description: String,
    val targetVolumeKg: Double,
    val currentVolumeKg: Double,
    val xpReward: Int,
    val attributeType: String, // "STR", "AGI", "END"
    val attributeReward: Int,
    val isDefeated: Boolean = false,
    val expiresAt: Long
)

@Entity(tableName = "weight_logs")
data class WeightEntity(
    @PrimaryKey val id: String,
    val weightKg: Double,
    val note: String = "",
    val timestamp: Long
)

