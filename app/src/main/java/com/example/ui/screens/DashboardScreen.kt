package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.components.BossQuestCard
import com.example.ui.components.DailyRoutineSection
import com.example.ui.components.HeaderComponent
import com.example.ui.components.HeroAttributesCard
import com.example.ui.components.TrendChartComponent
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    userProfile: UserProfile,
    workouts: List<WorkoutLog>,
    nutritionLogs: List<NutritionLog>,
    weightLogs: List<WeightLog>,
    goals: NutritionGoals,
    dailyRoutines: List<DailyRoutine>,
    bossQuest: BossQuest?,
    onToggleRoutine: (String) -> Unit,
    onLogWeight: (weightKg: Double, note: String) -> Unit,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToNutrition: () -> Unit,
    onNavigateToAiCoach: () -> Unit,
    onOpenProModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCaloriesToday = remember(nutritionLogs) { nutritionLogs.sumOf { it.calories } }
    val totalProteinToday = remember(nutritionLogs) { nutritionLogs.sumOf { it.proteinG } }
    val totalCarbsToday = remember(nutritionLogs) { nutritionLogs.sumOf { it.carbsG } }
    val totalFatsToday = remember(nutritionLogs) { nutritionLogs.sumOf { it.fatsG } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NeonBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title Bar & Top Controls
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NEONFIT MATRIX",
                        color = NeonCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "DAILY PROTOCOLS & RPG PROGRESSION",
                        color = TextTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pro Badge or Upgrade trigger
                    IconButton(
                        onClick = onOpenProModal,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (userProfile.isProUnlocked) NeonAmber.copy(alpha = 0.25f)
                                else NeonSurfaceCard
                            )
                            .border(1.dp, NeonAmber, CircleShape)
                            .testTag("pro_badge_icon_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Pro Status",
                            tint = NeonAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // AI Coach Uplink Button
                    IconButton(
                        onClick = onNavigateToAiCoach,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NeonMagenta.copy(alpha = 0.2f))
                            .border(1.dp, NeonMagenta, CircleShape)
                            .testTag("open_ai_coach_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Coach",
                            tint = NeonMagenta,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Cyberpunk Graphic Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_cyber_hero),
                    contentDescription = "Cyberpunk Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // High-tech cinematic dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    NeonBackground.copy(alpha = 0.92f),
                                    NeonBackground.copy(alpha = 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Banner Content Overlay
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SYSTEM ACTIVE • HYPERTROPHY PROTOCOL",
                            color = NeonGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "OPTIMIZE YOUR\nBIOLOGICAL LIMITS",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 20.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Header Component (Level, Streak, XP Bar)
        item {
            HeaderComponent(userProfile = userProfile)
        }

        // RPG Attribute Points System (STR, AGI, END)
        item(key = "hero_attributes") {
            HeroAttributesCard(userProfile = userProfile)
        }

        // Weekly Boss Quest (Bench Press Golem)
        item(key = "weekly_boss_quest") {
            BossQuestCard(bossQuest = bossQuest)
        }

        // 30-Day Biometric Telemetry & Volume Consistency Data Visualization
        item(key = "biometric_trend_chart") {
            TrendChartComponent(
                weightLogs = weightLogs,
                workouts = workouts,
                onLogWeight = onLogWeight
            )
        }

        // Daily Routine Protocols Section (Habit Tracking)
        item(key = "daily_routines_section") {
            DailyRoutineSection(
                routines = dailyRoutines,
                onToggleRoutine = onToggleRoutine
            )
        }

        // Quick Stats Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "TOTAL VOLUME",
                    value = String.format("%.0f kg", userProfile.totalVolumeKg),
                    accentColor = NeonCyan,
                    icon = Icons.Default.FitnessCenter,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "WORKOUTS",
                    value = "${userProfile.totalWorkoutsLogged}",
                    accentColor = NeonGreen,
                    icon = Icons.Default.Bolt,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "MEALS",
                    value = "${userProfile.totalMealsLogged}",
                    accentColor = NeonMagenta,
                    icon = Icons.Default.Restaurant,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Daily Macro Snapshot Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToNutrition() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NeonSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(NeonSurfaceCardBorder, NeonGreen.copy(alpha = 0.3f))
                    ),
                    width = 1.dp
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TODAY'S FUEL SYNTHESIS",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "View Nutrition →",
                            color = NeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    MacroBar(
                        label = "Calories",
                        current = totalCaloriesToday,
                        target = goals.targetCalories,
                        unit = "kcal",
                        color = NeonGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            MacroBar(
                                label = "Protein",
                                current = totalProteinToday,
                                target = goals.targetProtein,
                                unit = "g",
                                color = NeonCyan
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            MacroBar(
                                label = "Carbs",
                                current = totalCarbsToday,
                                target = goals.targetCarbs,
                                unit = "g",
                                color = NeonAmber
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            MacroBar(
                                label = "Fats",
                                current = totalFatsToday,
                                target = goals.targetFats,
                                unit = "g",
                                color = NeonMagenta
                            )
                        }
                    }
                }
            }
        }

        // Action Quick Nav Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToWorkouts,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("dashboard_log_workout_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = NeonBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ WORKOUT",
                        color = NeonBackground,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onNavigateToNutrition,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("dashboard_log_meal_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = NeonBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ MEAL",
                        color = NeonBackground,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Achievements & Badges Showcase Header
        item(key = "badges_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CYBERNETIC BADGES",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${userProfile.unlockedBadgeIds.size} / ${APP_BADGES.size} Unlocked",
                    color = NeonAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Lazy-loaded individual badge items with stable keys
        items(APP_BADGES, key = { "badge_${it.id}" }) { badge ->
            val isUnlocked = userProfile.unlockedBadgeIds.contains(badge.id)
            BadgeRowItem(badge = badge, isUnlocked = isUnlocked)
        }

        // Recent Workouts Activity
        if (workouts.isNotEmpty()) {
            item(key = "workouts_header") {
                Text(
                    text = "RECENT WORKOUT SESSIONS",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            items(workouts.take(3), key = { "workout_${it.id}" }) { workout ->
                WorkoutMiniCard(workout = workout)
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NeonSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(accentColor.copy(alpha = 0.5f), Color.Transparent)
            ),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
            Text(
                text = title,
                color = TextTertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun MacroBar(
    label: String,
    current: Int,
    target: Int,
    unit: String,
    color: Color
) {
    val progress = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$current / $target $unit",
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF1E2232))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun BadgeRowItem(
    badge: Badge,
    isUnlocked: Boolean
) {
    val borderColor = if (isUnlocked) NeonAmber else NeonSurfaceCardBorder
    val bgColor = if (isUnlocked) NeonSurfaceCard else NeonSurface.copy(alpha = 0.5f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = badge.iconEmoji,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = badge.title,
                    color = if (isUnlocked) TextPrimary else TextTertiary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                if (isUnlocked) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "UNLOCKED",
                        color = NeonAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Text(
                text = badge.description,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        Text(
            text = "+${badge.xpReward} XP",
            color = if (isUnlocked) NeonCyan else TextTertiary,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
fun WorkoutMiniCard(workout: WorkoutLog) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NeonSurface)
            .border(1.dp, NeonSurfaceCardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = workout.exerciseName,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = "${workout.muscleGroup} • ${workout.sets} sets × ${workout.reps} reps @ ${workout.weightKg} kg",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        Text(
            text = String.format("%.0f kg Vol", workout.totalVolumeKg),
            color = NeonCyan,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
        )
    }
}
