package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MuscleGroup
import com.example.model.UserProfile
import com.example.model.WeightLog
import com.example.model.WorkoutLog
import com.example.ui.components.HeaderComponent
import com.example.ui.components.TrendChartComponent
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutScreen(
    userProfile: UserProfile,
    workoutLogs: List<WorkoutLog>,
    weightLogs: List<WeightLog> = emptyList(),
    onLogWorkout: (exerciseName: String, muscleGroup: String, weightKg: Double, sets: Int, reps: Int) -> Unit,
    onDeleteWorkout: (logId: String) -> Unit,
    onLogWeight: ((weightKg: Double, note: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedMuscle by remember { mutableStateOf(MuscleGroup.CHEST) }
    var exerciseName by remember { mutableStateOf("Flat Barbell Bench") }
    var weightInput by remember { mutableStateOf("70.0") }
    var sets by remember { mutableIntStateOf(4) }
    var reps by remember { mutableIntStateOf(8) }

    val weight by remember {
        derivedStateOf { weightInput.toDoubleOrNull() ?: 0.0 }
    }
    val calculatedVolume by remember {
        derivedStateOf { weight * sets * reps }
    }

    val timeFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NeonBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeaderComponent(userProfile = userProfile)
        }

        // Section Title
        item {
            Column {
                Text(
                    text = "WORKOUT OVERLOAD PROTOCOL",
                    color = NeonCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Select muscle, configure telemetry, and log session volume",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Workout Logger Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("workout_logger_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NeonSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(NeonCyan.copy(alpha = 0.7f), NeonSurfaceCardBorder)
                    ),
                    width = 1.dp
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Muscle Group Chips
                    Text(
                        text = "TARGET MUSCLE GROUP",
                        color = TextTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MuscleGroup.values().forEach { muscle ->
                            val isSelected = muscle == selectedMuscle
                            val chipBg = if (isSelected) NeonCyan else NeonSurfaceCard
                            val chipTextColor = if (isSelected) NeonBackground else TextSecondary
                            val borderColor = if (isSelected) NeonCyan else NeonSurfaceCardBorder

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(chipBg)
                                    .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedMuscle = muscle
                                        exerciseName = muscle.exercises.firstOrNull() ?: ""
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = muscle.displayName,
                                    color = chipTextColor,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Exercise Name Suggestions
                    Text(
                        text = "RECOMMENDED MOVEMENTS",
                        color = TextTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        selectedMuscle.exercises.forEach { ex ->
                            val isChosen = exerciseName.equals(ex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChosen) NeonCyan.copy(alpha = 0.2f) else Color(0xFF1E2232))
                                    .border(
                                        1.dp,
                                        if (isChosen) NeonCyan else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { exerciseName = ex }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = ex,
                                    color = if (isChosen) NeonCyan else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Exercise Name Custom Input
                    OutlinedTextField(
                        value = exerciseName,
                        onValueChange = { exerciseName = it },
                        label = { Text("Exercise Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("exercise_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = NeonSurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = NeonSurfaceCard,
                            unfocusedContainerColor = NeonSurfaceCard
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Weight (kg) Input
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("exercise_weight_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = NeonSurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = NeonSurfaceCard,
                            unfocusedContainerColor = NeonSurfaceCard
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sets & Reps Counters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Sets counter
                        CounterField(
                            label = "SETS",
                            value = sets,
                            onIncrement = { sets++ },
                            onDecrement = { if (sets > 1) sets-- },
                            modifier = Modifier.weight(1f)
                        )

                        // Reps counter
                        CounterField(
                            label = "REPS",
                            value = reps,
                            onIncrement = { reps++ },
                            onDecrement = { if (reps > 1) reps-- },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic Total Volume Live Calculation Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NeonSurfaceCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(
                                listOf(NeonCyan.copy(alpha = 0.5f), NeonMagenta.copy(alpha = 0.5f))
                            ),
                            width = 1.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TOTAL CALCULATED VOLUME",
                                    color = TextTertiary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "$weight kg × $sets sets × $reps reps",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = String.format("%.0f KG", calculatedVolume),
                                color = NeonCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                modifier = Modifier.testTag("calculated_volume_text")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Log Workout Button (+50 XP)
                    Button(
                        onClick = {
                            if (exerciseName.isNotBlank() && weight > 0) {
                                onLogWorkout(exerciseName, selectedMuscle.displayName, weight, sets, reps)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_log_workout_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = NeonBackground,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LOG WORKOUT (+50 XP)",
                            color = NeonBackground,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // 30-Day Training Volume & Weight Visualization Component
        if (weightLogs.isNotEmpty() || workoutLogs.isNotEmpty()) {
            item(key = "workout_screen_trend_chart") {
                TrendChartComponent(
                    weightLogs = weightLogs,
                    workouts = workoutLogs,
                    onLogWeight = onLogWeight ?: { _, _ -> }
                )
            }
        }

        // Workout Logs History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOGGED WORKOUTS (${workoutLogs.size})",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        if (workoutLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No workout telemetry logged yet. Execute your first set above!",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(workoutLogs, key = { it.id }) { log ->
                WorkoutLogItem(
                    log = log,
                    timeFormatted = timeFormat.format(Date(log.timestamp)),
                    onDelete = { onDeleteWorkout(log.id) }
                )
            }
        }
    }
}

@Composable
fun CounterField(
    label: String,
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(NeonSurfaceCard)
            .border(1.dp, NeonSurfaceCardBorder, RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = TextTertiary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = "$value",
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(
                onClick = onIncrement,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun WorkoutLogItem(
    log: WorkoutLog,
    timeFormatted: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NeonSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(NeonSurfaceCardBorder, NeonCyan.copy(alpha = 0.3f))
            ),
            width = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = log.exerciseName,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = log.muscleGroup,
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${log.sets} sets × ${log.reps} reps @ ${log.weightKg} kg",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = timeFormatted,
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.0f kg", log.totalVolumeKg),
                    color = NeonCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = "Total Vol",
                    color = TextTertiary,
                    fontSize = 10.sp
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Log",
                        tint = DangerNeon,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
