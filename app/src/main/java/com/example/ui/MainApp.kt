package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.GeminiCoachService
import com.example.data.FitnessRepository
import com.example.model.ChatMessage
import com.example.ui.components.ProUpgradeModal
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    WORKOUTS("Workouts", Icons.Default.FitnessCenter),
    NUTRITION("Nutrition", Icons.Default.Restaurant),
    STORE("Arsenal", Icons.Default.ShoppingBag),
    AI_COACH("AI Coach", Icons.Default.SmartToy)
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val repository = remember { FitnessRepository(context) }
    val aiService = remember { GeminiCoachService() }
    val coroutineScope = rememberCoroutineScope()

    val userProfile by repository.userProfile.collectAsStateWithLifecycle()
    val workouts by repository.workouts.collectAsStateWithLifecycle()
    val nutritionLogs by repository.nutritionLogs.collectAsStateWithLifecycle()
    val nutritionGoals by repository.nutritionGoals.collectAsStateWithLifecycle()
    val weightLogs by repository.weightLogs.collectAsStateWithLifecycle()
    val dailyRoutines by repository.dailyRoutines.collectAsStateWithLifecycle()
    val bossQuest by repository.bossQuest.collectAsStateWithLifecycle()
    val levelUpEvent by repository.levelUpEvent.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }
    var showProModal by remember { mutableStateOf(false) }

    // Dynamic Skin HUD Palette
    val skinAccent = remember(userProfile.activeSkinId) {
        when (userProfile.activeSkinId) {
            "skin_cyber_gold" -> NeonAmber
            "skin_toxic_biohazard" -> NeonGreen
            "skin_plasma_purple" -> NeonPurple
            else -> NeonCyan
        }
    }

    // Dynamic Matrix Wallpaper Background Tint
    val backgroundBrush = remember(userProfile.activeWallpaperId) {
        when (userProfile.activeWallpaperId) {
            "wp_neon_matrix" -> Brush.verticalGradient(
                listOf(Color(0xFF041009), NeonBackground, Color(0xFF020805))
            )
            "wp_solar_forge" -> Brush.verticalGradient(
                listOf(Color(0xFF140505), NeonBackground, Color(0xFF0F0202))
            )
            "wp_quantum_nexus" -> Brush.verticalGradient(
                listOf(Color(0xFF020C17), NeonBackground, Color(0xFF0A0214))
            )
            else -> Brush.verticalGradient(
                listOf(NeonBackground, Color(0xFF0A0C14))
            )
        }
    }

    // Chat State with Room DB Integration
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "welcome",
                sender = "model",
                text = "⚡ CyberCoach online. Neural link established with local Room database. Biometric telemetry, hero attributes (STR/AGI/END), and Boss Quest status active. What's your objective today?"
            )
        )
    }
    var isChatLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Active Screen
        when (currentTab) {
            AppTab.DASHBOARD -> DashboardScreen(
                userProfile = userProfile,
                workouts = workouts,
                nutritionLogs = nutritionLogs,
                weightLogs = weightLogs,
                goals = nutritionGoals,
                dailyRoutines = dailyRoutines,
                bossQuest = bossQuest,
                onToggleRoutine = { id -> repository.toggleDailyRoutine(id) },
                onLogWeight = { w, note -> repository.logWeight(w, note) },
                onNavigateToWorkouts = { currentTab = AppTab.WORKOUTS },
                onNavigateToNutrition = { currentTab = AppTab.NUTRITION },
                onNavigateToAiCoach = { currentTab = AppTab.AI_COACH },
                onOpenProModal = { showProModal = true }
            )
            AppTab.WORKOUTS -> WorkoutScreen(
                userProfile = userProfile,
                workoutLogs = workouts,
                weightLogs = weightLogs,
                onLogWorkout = { ex, muscle, w, s, r ->
                    repository.logWorkout(ex, muscle, w, s, r)
                },
                onDeleteWorkout = { id ->
                    repository.deleteWorkout(id)
                },
                onLogWeight = { w, note -> repository.logWeight(w, note) }
            )
            AppTab.NUTRITION -> NutritionScreen(
                userProfile = userProfile,
                nutritionLogs = nutritionLogs,
                goals = nutritionGoals,
                onLogMeal = { name, cal, p, c, f, serving, fiber, sugar, sod, pot, calc, iron, vc, vd, mg, zn, bc ->
                    repository.logNutrition(
                        mealName = name,
                        calories = cal,
                        proteinG = p,
                        carbsG = c,
                        fatsG = f,
                        servingSizeG = serving,
                        fiberG = fiber,
                        sugarG = sugar,
                        sodiumMg = sod,
                        potassiumMg = pot,
                        calciumMg = calc,
                        ironMg = iron,
                        vitaminCIu = vc,
                        vitaminDIu = vd,
                        magnesiumMg = mg,
                        zincMg = zn,
                        barcode = bc
                    )
                },
                onDeleteMeal = { id ->
                    repository.deleteNutrition(id)
                }
            )
            AppTab.STORE -> StoreScreen(
                userProfile = userProfile,
                onPurchaseItem = { item -> repository.purchaseShopItem(item) },
                onEquipItem = { item -> repository.equipShopItem(item) },
                onOpenProModal = { showProModal = true }
            )
            AppTab.AI_COACH -> AiCoachScreen(
                messages = chatMessages,
                isLoading = isChatLoading,
                onSendMessage = { query ->
                    val userMsg = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        sender = "user",
                        text = query
                    )
                    chatMessages.add(userMsg)
                    isChatLoading = true
                    coroutineScope.launch {
                        // Comprehensive real-time telemetry from Room Database
                        val recentWorkoutsSummary = workouts.take(3).joinToString("; ") {
                            "${it.exerciseName} (${it.sets}x${it.reps} @ ${it.weightKg}kg)"
                        }
                        val todayCalories = nutritionLogs.sumOf { it.calories }
                        val todayProtein = nutritionLogs.sumOf { it.proteinG }
                        val bossInfo = bossQuest?.let {
                            "${it.bossName}: ${(it.progressFraction * 100).toInt()}% damaged (${it.currentVolumeKg.toInt()} / ${it.targetVolumeKg.toInt()} kg)"
                        } ?: "None"

                        val todayFiber = nutritionLogs.sumOf { it.fiberG }
                        val todaySodium = nutritionLogs.sumOf { it.sodiumMg }
                        val todayPotassium = nutritionLogs.sumOf { it.potassiumMg }
                        val todayVitD = nutritionLogs.sumOf { it.vitaminDIu }

                        val userContext = """
                            User Level: ${userProfile.level} (${userProfile.levelTitle})
                            Streak: ${userProfile.streakDays} days
                            Attributes: STR=${userProfile.strength}, AGI=${userProfile.agility}, END=${userProfile.endurance}
                            Total Volume: ${userProfile.totalVolumeKg} kg across ${userProfile.totalWorkoutsLogged} sessions
                            Recent Workouts: ${if (recentWorkoutsSummary.isEmpty()) "None logged yet" else recentWorkoutsSummary}
                            Today's Nutrition: $todayCalories kcal, ${todayProtein}g protein (Goals: ${nutritionGoals.targetCalories} kcal, ${nutritionGoals.targetProtein}g protein)
                            Micronutrients Today: Fiber=${todayFiber}g, Sodium=${todaySodium.toInt()}mg, Potassium=${todayPotassium.toInt()}mg, Vit D=${todayVitD.toInt()}IU
                            Active Boss Quest: $bossInfo
                            Pro Tier: ${if (userProfile.isProUnlocked) "UNLOCKED" else "FREE"}
                        """.trimIndent()

                        val reply = aiService.sendMessage(
                            history = chatMessages.toList(),
                            userMessage = query,
                            userContext = userContext
                        )
                        chatMessages.add(reply)
                        isChatLoading = false
                    }
                }
            )
        }

        // Sleek Dark-Neon Bottom Navigation Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .testTag("bottom_navigation_bar"),
            color = NeonSurface,
            tonalElevation = 8.dp,
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                    listOf(skinAccent.copy(alpha = 0.4f), NeonMagenta.copy(alpha = 0.3f))
                ),
                width = 1.dp
            )
        ) {
            NavigationBar(
                containerColor = NeonSurface,
                contentColor = TextPrimary,
                modifier = Modifier.height(68.dp)
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = tab == currentTab
                    val activeColor = when (tab) {
                        AppTab.NUTRITION -> NeonMagenta
                        AppTab.STORE -> NeonAmber
                        AppTab.AI_COACH -> NeonPurple
                        else -> skinAccent
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) activeColor else TextTertiary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                color = if (isSelected) activeColor else TextTertiary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = activeColor,
                            unselectedIconColor = TextTertiary,
                            indicatorColor = activeColor.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }

        // Pro Upgrade Modal with Charity Pledge
        if (showProModal) {
            ProUpgradeModal(
                userProfile = userProfile,
                onDismiss = { showProModal = false },
                onConfirmUpgrade = {
                    repository.unlockProTier()
                    showProModal = false
                }
            )
        }

        // Level Up Trigger Celebration Dialog
        AnimatedVisibility(
            visible = levelUpEvent != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            levelUpEvent?.let { newLevel ->
                Dialog(onDismissRequest = { repository.dismissLevelUpEvent() }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .testTag("level_up_dialog"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = NeonSurface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                listOf(skinAccent, NeonMagenta, NeonAmber)
                            ),
                            width = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(NeonAmber.copy(alpha = 0.4f), Color.Transparent)
                                        )
                                    )
                                    .border(2.dp, NeonAmber, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Trophy",
                                    tint = NeonAmber,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "LEVEL UP REACHED!",
                                color = skinAccent,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "You have advanced to Level $newLevel: ${userProfile.levelTitle}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "+300 XP Bonus Awarded • Attribute Multiplier Activated",
                                color = NeonAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { repository.dismissLevelUpEvent() },
                                colors = ButtonDefaults.buttonColors(containerColor = skinAccent),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "CLAIM & CONTINUE",
                                    color = NeonBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
