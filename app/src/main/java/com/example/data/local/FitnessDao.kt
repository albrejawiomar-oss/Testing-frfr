package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {
    // Workouts
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    suspend fun getAllWorkoutsList(): List<WorkoutEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteWorkoutById(id: String)

    // Nutrition
    @Query("SELECT * FROM nutrition_logs ORDER BY timestamp DESC")
    fun getAllNutrition(): Flow<List<NutritionEntity>>

    @Query("SELECT * FROM nutrition_logs ORDER BY timestamp DESC")
    suspend fun getAllNutritionList(): List<NutritionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutrition(nutrition: NutritionEntity)

    @Query("DELETE FROM nutrition_logs WHERE id = :id")
    suspend fun deleteNutritionById(id: String)

    // Daily Routines
    @Query("SELECT * FROM daily_routines WHERE dateString = :date ORDER BY category ASC")
    fun getRoutinesForDate(date: String): Flow<List<DailyRoutineEntity>>

    @Query("SELECT * FROM daily_routines WHERE dateString = :date")
    suspend fun getRoutinesForDateList(date: String): List<DailyRoutineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<DailyRoutineEntity>)

    @Query("UPDATE daily_routines SET isCompleted = :completed WHERE id = :id")
    suspend fun updateRoutineCompletion(id: String, completed: Boolean)

    // Boss Quests
    @Query("SELECT * FROM boss_quests ORDER BY expiresAt DESC")
    fun getAllBossQuests(): Flow<List<BossQuestEntity>>

    @Query("SELECT * FROM boss_quests WHERE isDefeated = 0 LIMIT 1")
    suspend fun getActiveBossQuest(): BossQuestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBossQuest(quest: BossQuestEntity)

    @Query("UPDATE boss_quests SET currentVolumeKg = :current, isDefeated = :defeated WHERE id = :id")
    suspend fun updateBossProgress(id: String, current: Double, defeated: Boolean)

    // Weight Logs
    @Query("SELECT * FROM weight_logs ORDER BY timestamp ASC")
    fun getAllWeightLogs(): Flow<List<WeightEntity>>

    @Query("SELECT * FROM weight_logs ORDER BY timestamp ASC")
    suspend fun getAllWeightLogsList(): List<WeightEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(weight: WeightEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLogs(weights: List<WeightEntity>)

    @Query("DELETE FROM weight_logs WHERE id = :id")
    suspend fun deleteWeightLogById(id: String)
}
