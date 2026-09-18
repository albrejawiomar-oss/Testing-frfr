package com.example.data

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.BossQuestEntity
import com.example.data.local.DailyRoutineEntity
import com.example.data.local.NutritionEntity
import com.example.data.local.WorkoutEntity
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

class FitnessRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.fitnessDao()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val prefs = context.getSharedPreferences("neon_fit_prefs", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _workouts = MutableStateFlow<List<WorkoutLog>>(emptyList())
    val workouts: StateFlow<List<WorkoutLog>> = _workouts.asStateFlow()

    private val _nutritionLogs = MutableStateFlow<List<NutritionLog>>(emptyList())
    val nutritionLogs: StateFlow<List<NutritionLog>> = _nutritionLogs.asStateFlow()

    private val _nutritionGoals = MutableStateFlow(NutritionGoals())
    val nutritionGoals: StateFlow<NutritionGoals> = _nutritionGoals.asStateFlow()

    private val _dailyRoutines = MutableStateFlow<List<DailyRoutine>>(emptyList())
    val dailyRoutines: StateFlow<List<DailyRoutine>> = _dailyRoutines.asStateFlow()

    private val _bossQuest = MutableStateFlow<BossQuest?>(null)
    val bossQuest: StateFlow<BossQuest?> = _bossQuest.asStateFlow()

    private val _weightLogs = MutableStateFlow<List<WeightLog>>(emptyList())
    val weightLogs: StateFlow<List<WeightLog>> = _weightLogs.asStateFlow()

    private val _levelUpEvent = MutableStateFlow<Int?>(null)
    val levelUpEvent: StateFlow<Int?> = _levelUpEvent.asStateFlow()

    init {
        loadData()
        evaluateDailyStreak()
        initRoomDatabaseFlows()
    }

    companion object {
        fun xpRequiredForLevel(level: Int): Int {
            return (100.0 * level.toDouble().pow(1.5)).toInt().coerceAtLeast(100)
        }
    }

    private fun initRoomDatabaseFlows() {
        scope.launch {
            // Collect workouts from Room
            dao.getAllWorkouts().collect { entities ->
                val logs = entities.map {
                    WorkoutLog(
                        id = it.id,
                        exerciseName = it.exerciseName,
                        muscleGroup = it.muscleGroup,
                        weightKg = it.weightKg,
                        sets = it.sets,
                        reps = it.reps,
                        totalVolumeKg = it.totalVolumeKg,
                        timestamp = it.timestamp
                    )
                }
                _workouts.value = logs
            }
        }

        scope.launch {
            // Collect nutrition from Room
            dao.getAllNutrition().collect { entities ->
                val logs = entities.map {
                    NutritionLog(
                        id = it.id,
                        mealName = it.mealName,
                        calories = it.calories,
                        proteinG = it.proteinG,
                        carbsG = it.carbsG,
                        fatsG = it.fatsG,
                        timestamp = it.timestamp,
                        servingSizeG = it.servingSizeG,
                        fiberG = it.fiberG,
                        sugarG = it.sugarG,
                        sodiumMg = it.sodiumMg,
                        potassiumMg = it.potassiumMg,
                        calciumMg = it.calciumMg,
                        ironMg = it.ironMg,
                        vitaminCIu = it.vitaminCIu,
                        vitaminDIu = it.vitaminDIu,
                        magnesiumMg = it.magnesiumMg,
                        zincMg = it.zincMg,
                        barcode = it.barcode
                    )
                }
                _nutritionLogs.value = logs
            }
        }

        scope.launch {
            val todayStr = dateFormat.format(Date())
            // Ensure daily routines exist in Room for today
            val existingRoutines = dao.getRoutinesForDateList(todayStr)
            if (existingRoutines.isEmpty()) {
                val defaults = listOf(
                    DailyRoutineEntity(
                        id = "routine_push_pull",
                        title = "Push/Pull Hypertrophy Set (4x10)",
                        category = "WORKOUT",
                        xpReward = 60,
                        attributeType = "STR",
                        attributeReward = 2,
                        dateString = todayStr
                    ),
                    DailyRoutineEntity(
                        id = "routine_cardio_drill",
                        title = "15-Min High-Cadence Cardio / Interval Drill",
                        category = "WORKOUT",
                        xpReward = 40,
                        attributeType = "AGI",
                        attributeReward = 3,
                        dateString = todayStr
                    ),
                    DailyRoutineEntity(
                        id = "routine_protein_goal",
                        title = "Hit Daily 180g Protein Target",
                        category = "NUTRITION",
                        xpReward = 50,
                        attributeType = "END",
                        attributeReward = 2,
                        dateString = todayStr
                    ),
                    DailyRoutineEntity(
                        id = "routine_hydration",
                        title = "Electrolyte Intra-Workout Synthesis (2.5L Water)",
                        category = "RECOVERY",
                        xpReward = 30,
                        attributeType = "END",
                        attributeReward = 1,
                        dateString = todayStr
                    )
                )
                dao.insertRoutines(defaults)
            }

            dao.getRoutinesForDate(todayStr).collect { entities ->
                _dailyRoutines.value = entities.map {
                    DailyRoutine(
                        id = it.id,
                        title = it.title,
                        category = it.category,
                        xpReward = it.xpReward,
                        attributeType = it.attributeType,
                        attributeReward = it.attributeReward,
                        isCompleted = it.isCompleted
                    )
                }
            }
        }

        scope.launch {
            // Ensure weekly boss quest exists
            val activeQuest = dao.getActiveBossQuest()
            if (activeQuest == null) {
                val defaultBoss = BossQuestEntity(
                    id = "boss_bench_golem_w1",
                    title = "Weekly Community Boss Raid",
                    bossName = "Bench Press Golem",
                    description = "Defeat the ancient iron construct by accumulating 5,000 kg total workout volume this week.",
                    targetVolumeKg = 5000.0,
                    currentVolumeKg = 0.0,
                    xpReward = 300,
                    attributeType = "STR",
                    attributeReward = 15,
                    isDefeated = false,
                    expiresAt = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
                )
                dao.insertBossQuest(defaultBoss)
            }

            dao.getAllBossQuests().collect { list ->
                val current = list.firstOrNull()
                if (current != null) {
                    _bossQuest.value = BossQuest(
                        id = current.id,
                        title = current.title,
                        bossName = current.bossName,
                        description = current.description,
                        targetVolumeKg = current.targetVolumeKg,
                        currentVolumeKg = current.currentVolumeKg,
                        xpReward = current.xpReward,
                        attributeType = current.attributeType,
                        attributeReward = current.attributeReward,
                        isDefeated = current.isDefeated,
                        expiresAt = current.expiresAt
                    )
                }
            }
        }

        scope.launch {
            // Seed 30 days of historical weight telemetry if empty
            val existingWeights = dao.getAllWeightLogsList()
            if (existingWeights.isEmpty()) {
                val now = System.currentTimeMillis()
                val oneDayMs = 24 * 60 * 60 * 1000L
                // Starting weight 79.8 kg, fluctuating naturally downward to 77.2 kg
                val baseWeights = listOf(
                    79.8, 79.6, 79.7, 79.4, 79.5, 79.1, 79.2, 78.9, 78.8, 78.9,
                    78.6, 78.4, 78.5, 78.2, 78.3, 78.0, 77.9, 78.1, 77.8, 77.6,
                    77.7, 77.5, 77.3, 77.4, 77.2, 77.1, 77.3, 77.0, 77.1, 77.2
                )
                val initialWeights = (29 downTo 0).mapIndexed { index, daysAgo ->
                    val weightVal = baseWeights.getOrElse(index) { 77.5 }
                    com.example.data.local.WeightEntity(
                        id = UUID.randomUUID().toString(),
                        weightKg = weightVal,
                        note = if (daysAgo == 0) "Current weigh-in" else "Morning weigh-in",
                        timestamp = now - (daysAgo * oneDayMs)
                    )
                }
                dao.insertWeightLogs(initialWeights)
            }

            dao.getAllWeightLogs().collect { entities ->
                _weightLogs.value = entities.map {
                    WeightLog(
                        id = it.id,
                        weightKg = it.weightKg,
                        note = it.note,
                        timestamp = it.timestamp
                    )
                }
            }
        }
    }

    private fun loadData() {
        val level = prefs.getInt("level", 1)
        val xp = prefs.getInt("xp", 0)
        val streak = prefs.getInt("streak_days", 1)
        val streakFreezes = prefs.getInt("streak_freezes", 1)
        val lastDate = prefs.getString("last_active_date", "") ?: ""
        val volume = prefs.getFloat("total_volume", 0f).toDouble()
        val workoutsCount = prefs.getInt("workouts_count", 0)
        val mealsCount = prefs.getInt("meals_count", 0)
        val badgesJson = prefs.getString("unlocked_badges", "[]") ?: "[]"
        val strength = prefs.getInt("str", 10)
        val agility = prefs.getInt("agi", 10)
        val endurance = prefs.getInt("end", 10)
        val purchasedJson = prefs.getString("purchased_items", "[\"skin_neon_striker\", \"wp_cyber_grid\"]") ?: "[]"
        val activeSkin = prefs.getString("active_skin", "skin_neon_striker") ?: "skin_neon_striker"
        val activeWallpaper = prefs.getString("active_wp", "wp_cyber_grid") ?: "wp_cyber_grid"
        val isPro = prefs.getBoolean("is_pro_unlocked", false)

        val unlockedBadges = mutableListOf<String>()
        try {
            val arr = JSONArray(badgesJson)
            for (i in 0 until arr.length()) {
                unlockedBadges.add(arr.getString(i))
            }
        } catch (_: Exception) {}

        val purchasedList = mutableListOf<String>()
        try {
            val arr = JSONArray(purchasedJson)
            for (i in 0 until arr.length()) {
                purchasedList.add(arr.getString(i))
            }
        } catch (_: Exception) {}
        if (purchasedList.isEmpty()) {
            purchasedList.addAll(listOf("skin_neon_striker", "wp_cyber_grid"))
        }

        _userProfile.value = UserProfile(
            level = level,
            xp = xp,
            streakDays = streak,
            streakFreezes = streakFreezes,
            lastActiveDate = lastDate,
            totalVolumeKg = volume,
            totalWorkoutsLogged = workoutsCount,
            totalMealsLogged = mealsCount,
            unlockedBadgeIds = unlockedBadges,
            strength = strength,
            agility = agility,
            endurance = endurance,
            purchasedItemIds = purchasedList,
            activeSkinId = activeSkin,
            activeWallpaperId = activeWallpaper,
            isProUnlocked = isPro
        )
    }

    private fun saveData() {
        val p = _userProfile.value
        prefs.edit()
            .putInt("level", p.level)
            .putInt("xp", p.xp)
            .putInt("streak_days", p.streakDays)
            .putInt("streak_freezes", p.streakFreezes)
            .putString("last_active_date", p.lastActiveDate)
            .putFloat("total_volume", p.totalVolumeKg.toFloat())
            .putInt("workouts_count", p.totalWorkoutsLogged)
            .putInt("meals_count", p.totalMealsLogged)
            .putInt("str", p.strength)
            .putInt("agi", p.agility)
            .putInt("end", p.endurance)
            .putString("active_skin", p.activeSkinId)
            .putString("active_wp", p.activeWallpaperId)
            .putBoolean("is_pro_unlocked", p.isProUnlocked)
            .putString("unlocked_badges", JSONArray(p.unlockedBadgeIds).toString())
            .putString("purchased_items", JSONArray(p.purchasedItemIds).toString())
            .apply()
    }

    fun dismissLevelUpEvent() {
        _levelUpEvent.value = null
    }

    private fun evaluateDailyStreak() {
        val todayStr = dateFormat.format(Date())
        val currentProfile = _userProfile.value
        val lastDateStr = currentProfile.lastActiveDate

        if (lastDateStr.isEmpty()) {
            _userProfile.value = currentProfile.copy(lastActiveDate = todayStr, streakDays = 1)
            saveData()
            return
        }

        if (lastDateStr == todayStr) return

        try {
            val lastDate = dateFormat.parse(lastDateStr)
            val today = dateFormat.parse(todayStr)
            if (lastDate != null && today != null) {
                val diffDays = ((today.time - lastDate.time) / (1000 * 60 * 60 * 24)).toInt()
                when {
                    diffDays == 1 -> {
                        val newStreak = currentProfile.streakDays + 1
                        val extraFreeze = if (newStreak % 7 == 0 && currentProfile.streakFreezes < 3) 1 else 0
                        _userProfile.value = currentProfile.copy(
                            lastActiveDate = todayStr,
                            streakDays = newStreak,
                            streakFreezes = currentProfile.streakFreezes + extraFreeze
                        )
                    }
                    diffDays == 2 && currentProfile.streakFreezes > 0 -> {
                        _userProfile.value = currentProfile.copy(
                            lastActiveDate = todayStr,
                            streakFreezes = currentProfile.streakFreezes - 1
                        )
                    }
                    else -> {
                        _userProfile.value = currentProfile.copy(
                            lastActiveDate = todayStr,
                            streakDays = 1
                        )
                    }
                }
                saveData()
            }
        } catch (_: Exception) {}
    }

    fun addXp(amount: Int) {
        val current = _userProfile.value
        var newXp = current.xp + amount
        var currentLevel = current.level
        var leveledUp = false

        while (true) {
            val required = xpRequiredForLevel(currentLevel)
            if (newXp >= required) {
                newXp -= required
                currentLevel += 1
                leveledUp = true
            } else {
                break
            }
        }

        _userProfile.value = current.copy(level = currentLevel, xp = newXp)
        if (leveledUp) {
            _levelUpEvent.value = currentLevel
        }
        checkBadges()
        saveData()
    }

    fun logWorkout(
        exerciseName: String,
        muscleGroup: String,
        weightKg: Double,
        sets: Int,
        reps: Int
    ) {
        val volume = weightKg * sets * reps
        val entity = WorkoutEntity(
            id = UUID.randomUUID().toString(),
            exerciseName = exerciseName,
            muscleGroup = muscleGroup,
            weightKg = weightKg,
            sets = sets,
            reps = reps,
            totalVolumeKg = volume,
            timestamp = System.currentTimeMillis()
        )

        scope.launch {
            dao.insertWorkout(entity)
        }

        val p = _userProfile.value
        // Attribute Logic:
        // Push / Pull / Legs volume -> Strength (STR)
        // Cardio / High reps -> Agility (AGI)
        val strGain = if (muscleGroup.uppercase() in listOf("CHEST", "BACK", "LEGS", "SHOULDERS", "ARMS")) {
            (volume / 500).toInt().coerceAtLeast(1)
        } else 0

        val agiGain = if (muscleGroup.uppercase() == "CARDIO" || reps >= 15) {
            (sets * 2).coerceAtLeast(1)
        } else 0

        _userProfile.value = p.copy(
            totalVolumeKg = p.totalVolumeKg + volume,
            totalWorkoutsLogged = p.totalWorkoutsLogged + 1,
            strength = p.strength + strGain,
            agility = p.agility + agiGain
        )

        // Progress Boss Quest if active
        val currentBoss = _bossQuest.value
        if (currentBoss != null && !currentBoss.isDefeated) {
            val newProgress = currentBoss.currentVolumeKg + volume
            val isDefeated = newProgress >= currentBoss.targetVolumeKg
            scope.launch {
                dao.updateBossProgress(currentBoss.id, newProgress, isDefeated)
            }
            if (isDefeated) {
                addXp(currentBoss.xpReward)
                _userProfile.value = _userProfile.value.copy(
                    strength = _userProfile.value.strength + currentBoss.attributeReward
                )
            }
        }

        evaluateDailyStreak()
        addXp(50)
    }

    fun deleteWorkout(logId: String) {
        val target = _workouts.value.find { it.id == logId } ?: return
        scope.launch {
            dao.deleteWorkoutById(logId)
        }
        val p = _userProfile.value
        _userProfile.value = p.copy(
            totalVolumeKg = (p.totalVolumeKg - target.totalVolumeKg).coerceAtLeast(0.0)
        )
        saveData()
    }

    fun logNutrition(
        mealName: String,
        calories: Int,
        proteinG: Int,
        carbsG: Int,
        fatsG: Int,
        servingSizeG: Double = 100.0,
        fiberG: Double = 0.0,
        sugarG: Double = 0.0,
        sodiumMg: Double = 0.0,
        potassiumMg: Double = 0.0,
        calciumMg: Double = 0.0,
        ironMg: Double = 0.0,
        vitaminCIu: Double = 0.0,
        vitaminDIu: Double = 0.0,
        magnesiumMg: Double = 0.0,
        zincMg: Double = 0.0,
        barcode: String = ""
    ) {
        val entity = NutritionEntity(
            id = UUID.randomUUID().toString(),
            mealName = mealName,
            calories = calories,
            proteinG = proteinG,
            carbsG = carbsG,
            fatsG = fatsG,
            timestamp = System.currentTimeMillis(),
            servingSizeG = servingSizeG,
            fiberG = fiberG,
            sugarG = sugarG,
            sodiumMg = sodiumMg,
            potassiumMg = potassiumMg,
            calciumMg = calciumMg,
            ironMg = ironMg,
            vitaminCIu = vitaminCIu,
            vitaminDIu = vitaminDIu,
            magnesiumMg = magnesiumMg,
            zincMg = zincMg,
            barcode = barcode
        )

        scope.launch {
            dao.insertNutrition(entity)
        }

        val p = _userProfile.value
        // Consistent calorie/macro logging -> Endurance (END)
        val endGain = if (proteinG >= 25) 2 else 1

        _userProfile.value = p.copy(
            totalMealsLogged = p.totalMealsLogged + 1,
            endurance = p.endurance + endGain
        )

        evaluateDailyStreak()
        addXp(20)
    }

    fun deleteNutrition(logId: String) {
        scope.launch {
            dao.deleteNutritionById(logId)
        }
        saveData()
    }

    fun toggleDailyRoutine(routineId: String) {
        val routine = _dailyRoutines.value.find { it.id == routineId } ?: return
        val newStatus = !routine.isCompleted
        scope.launch {
            dao.updateRoutineCompletion(routineId, newStatus)
        }

        if (newStatus) {
            addXp(routine.xpReward)
            val p = _userProfile.value
            _userProfile.value = when (routine.attributeType) {
                "STR" -> p.copy(strength = p.strength + routine.attributeReward)
                "AGI" -> p.copy(agility = p.agility + routine.attributeReward)
                "END" -> p.copy(endurance = p.endurance + routine.attributeReward)
                else -> p
            }
            saveData()
        }
    }

    fun purchaseShopItem(item: ShopItem): Boolean {
        val p = _userProfile.value
        if (p.purchasedItemIds.contains(item.id)) return true
        if (p.xp < item.costXp) return false

        val updatedPurchased = p.purchasedItemIds + item.id
        val updatedProfile = when (item.type) {
            ShopItemType.SKIN -> p.copy(
                xp = p.xp - item.costXp,
                purchasedItemIds = updatedPurchased,
                activeSkinId = item.id
            )
            ShopItemType.WALLPAPER -> p.copy(
                xp = p.xp - item.costXp,
                purchasedItemIds = updatedPurchased,
                activeWallpaperId = item.id
            )
        }
        _userProfile.value = updatedProfile
        saveData()
        return true
    }

    fun equipShopItem(item: ShopItem) {
        val p = _userProfile.value
        if (!p.purchasedItemIds.contains(item.id)) return
        val updatedProfile = when (item.type) {
            ShopItemType.SKIN -> p.copy(activeSkinId = item.id)
            ShopItemType.WALLPAPER -> p.copy(activeWallpaperId = item.id)
        }
        _userProfile.value = updatedProfile
        saveData()
    }

    fun unlockProTier() {
        val p = _userProfile.value
        _userProfile.value = p.copy(isProUnlocked = true)
        saveData()
    }

    private fun checkBadges() {
        val p = _userProfile.value
        val unlocked = p.unlockedBadgeIds.toMutableSet()
        var bonusXp = 0

        if (!unlocked.contains("first_blood") && p.totalWorkoutsLogged >= 1) {
            unlocked.add("first_blood")
            bonusXp += 50
        }
        if (!unlocked.contains("iron_will") && p.streakDays >= 7) {
            unlocked.add("iron_will")
            bonusXp += 150
        }
        if (!unlocked.contains("volume_behemoth") && p.totalVolumeKg >= 10000.0) {
            unlocked.add("volume_behemoth")
            bonusXp += 200
        }
        if (!unlocked.contains("fuel_master") && p.totalMealsLogged >= 5) {
            unlocked.add("fuel_master")
            bonusXp += 100
        }
        if (!unlocked.contains("golem_slayer") && (_bossQuest.value?.isDefeated == true)) {
            unlocked.add("golem_slayer")
            bonusXp += 300
        }
        if (!unlocked.contains("cyber_titan") && p.level >= 5) {
            unlocked.add("cyber_titan")
            bonusXp += 250
        }

        if (unlocked.size > p.unlockedBadgeIds.size) {
            _userProfile.value = p.copy(unlockedBadgeIds = unlocked.toList())
            if (bonusXp > 0) {
                addXp(bonusXp)
            }
        }
    }

    fun logWeight(weightKg: Double, note: String = "") {
        val entity = com.example.data.local.WeightEntity(
            id = UUID.randomUUID().toString(),
            weightKg = weightKg,
            note = note,
            timestamp = System.currentTimeMillis()
        )
        scope.launch {
            dao.insertWeightLog(entity)
        }
        addXp(25)
    }

    fun deleteWeight(id: String) {
        scope.launch {
            dao.deleteWeightLogById(id)
        }
    }
}
