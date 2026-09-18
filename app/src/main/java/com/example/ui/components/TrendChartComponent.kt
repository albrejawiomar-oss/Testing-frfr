package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DailyTrendPoint
import com.example.model.WeightLog
import com.example.model.WorkoutLog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

enum class ChartMetric(val title: String, val unit: String, val primaryColor: Color, val accentColor: Color) {
    WEIGHT("Weight Trends", "kg", NeonCyan, NeonMagenta),
    VOLUME("Workout Volume", "kg", NeonGreen, NeonCyan)
}

enum class ChartTimeRange(val label: String, val days: Int) {
    PAST_7_DAYS("7D", 7),
    PAST_14_DAYS("14D", 14),
    PAST_30_DAYS("30D", 30)
}

@Composable
fun TrendChartComponent(
    weightLogs: List<WeightLog>,
    workouts: List<WorkoutLog>,
    onLogWeight: (weightKg: Double, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMetric by remember { mutableStateOf(ChartMetric.WEIGHT) }
    var selectedTimeRange by remember { mutableStateOf(ChartTimeRange.PAST_30_DAYS) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }
    var showAddWeightDialog by remember { mutableStateOf(false) }

    // Aggregate past 30 days into discrete daily buckets
    val trendData = remember(weightLogs, workouts, selectedTimeRange) {
        computeTrendData(
            days = selectedTimeRange.days,
            weightLogs = weightLogs,
            workouts = workouts
        )
    }

    val currentWeight = remember(weightLogs) {
        weightLogs.maxByOrNull { it.timestamp }?.weightKg ?: 77.2
    }

    val past30DaysWeightDiff = remember(trendData) {
        val validPoints = trendData.mapNotNull { it.weightKg }
        if (validPoints.size >= 2) {
            val start = validPoints.first()
            val end = validPoints.last()
            end - start
        } else 0.0
    }

    val totalRangeVolume = remember(trendData) {
        trendData.sumOf { it.workoutVolumeKg }
    }

    val workoutDaysCount = remember(trendData) {
        trendData.count { it.hasWorkout }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trend_data_visualization_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NeonSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    selectedMetric.primaryColor.copy(alpha = 0.6f),
                    selectedMetric.accentColor.copy(alpha = 0.4f)
                )
            ),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with title and Log Weight Quick Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (selectedMetric == ChartMetric.WEIGHT) Icons.Default.MonitorWeight else Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = selectedMetric.primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "30-DAY BIOMETRIC TELEMETRY",
                            color = selectedMetric.primaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Text(
                        text = "Interactive Area & Bar Recharts-Engine",
                        color = TextTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = { showAddWeightDialog = true },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
                        .testTag("quick_log_weight_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Quick Log Weight",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metric Toggle Controls & Time Range Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Metric Selector (Weight vs Volume)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonSurfaceCard)
                        .border(1.dp, NeonSurfaceCardBorder, RoundedCornerShape(10.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ChartMetric.values().forEach { metric ->
                        val isSelected = selectedMetric == metric
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) metric.primaryColor else Color.Transparent)
                                .clickable {
                                    selectedMetric = metric
                                    selectedPointIndex = null
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("metric_pill_${metric.name.lowercase()}")
                        ) {
                            Text(
                                text = if (metric == ChartMetric.WEIGHT) "Weight" else "Volume",
                                color = if (isSelected) NeonBackground else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                            )
                        }
                    }
                }

                // Time Range Pills (7D, 14D, 30D)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonSurfaceCard)
                        .border(1.dp, NeonSurfaceCardBorder, RoundedCornerShape(10.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    ChartTimeRange.values().forEach { range ->
                        val isSelected = selectedTimeRange == range
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonSurfaceCardBorder else Color.Transparent)
                                .clickable {
                                    selectedTimeRange = range
                                    selectedPointIndex = null
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("range_pill_${range.label}")
                        ) {
                            Text(
                                text = range.label,
                                color = if (isSelected) TextPrimary else TextTertiary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stat Summary Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (selectedMetric == ChartMetric.WEIGHT) {
                    MiniTrendStat(
                        label = "CURRENT WEIGHT",
                        value = String.format("%.1f kg", currentWeight),
                        subtext = "Latest log",
                        accentColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MiniTrendStat(
                        label = "${selectedTimeRange.label} NET CHANGE",
                        value = String.format("%+.1f kg", past30DaysWeightDiff),
                        subtext = if (past30DaysWeightDiff <= 0) "Caloric Deficit" else "Hypertrophy Surplus",
                        accentColor = if (past30DaysWeightDiff <= 0) NeonGreen else NeonAmber,
                        icon = if (past30DaysWeightDiff <= 0) Icons.AutoMirrored.Filled.TrendingDown else Icons.AutoMirrored.Filled.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    MiniTrendStat(
                        label = "TOTAL RANGE VOLUME",
                        value = String.format("%.0f kg", totalRangeVolume),
                        subtext = "${selectedTimeRange.days} days cumulative",
                        accentColor = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MiniTrendStat(
                        label = "CONSISTENCY",
                        value = "$workoutDaysCount / ${selectedTimeRange.days} Days",
                        subtext = "${((workoutDaysCount.toFloat() / selectedTimeRange.days) * 100).toInt()}% Frequency",
                        accentColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Canvas Recharts Area Chart & Bar Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0C0E17))
                    .border(1.dp, NeonSurfaceCardBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                InteractiveRechartsCanvas(
                    data = trendData,
                    metric = selectedMetric,
                    selectedIndex = selectedPointIndex,
                    onSelectIndex = { selectedPointIndex = it }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tooltip or Active Telemetry Inspector Banner
            val inspectedPoint = selectedPointIndex?.let { trendData.getOrNull(it) }
            if (inspectedPoint != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(selectedMetric.primaryColor.copy(alpha = 0.12f))
                        .border(1.dp, selectedMetric.primaryColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(selectedMetric.primaryColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = inspectedPoint.dateLabel,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = if (selectedMetric == ChartMetric.WEIGHT) {
                            inspectedPoint.weightKg?.let { String.format("%.1f kg", it) } ?: "No weigh-in"
                        } else {
                            String.format("%.0f kg volume", inspectedPoint.workoutVolumeKg)
                        },
                        color = selectedMetric.primaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Touch/drag on chart to inspect discrete daily data points",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = "30-Day Window",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Modal to log new weight entry
    if (showAddWeightDialog) {
        AddWeightModal(
            currentWeight = currentWeight,
            onDismiss = { showAddWeightDialog = false },
            onConfirm = { weight, note ->
                onLogWeight(weight, note)
                showAddWeightDialog = false
            }
        )
    }
}

@Composable
fun MiniTrendStat(
    label: String,
    value: String,
    subtext: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(NeonSurfaceCard)
            .border(1.dp, NeonSurfaceCardBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Text(
            text = label,
            color = TextTertiary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtext,
            color = accentColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InteractiveRechartsCanvas(
    data: List<DailyTrendPoint>,
    metric: ChartMetric,
    selectedIndex: Int?,
    onSelectIndex: (Int) -> Unit
) {
    val transitionProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600),
        label = "chart_animation"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(data) {
                detectTapGestures { offset ->
                    if (data.isNotEmpty()) {
                        val spacing = size.width / data.size.toFloat()
                        val tappedIndex = (offset.x / spacing).toInt().coerceIn(0, data.lastIndex)
                        onSelectIndex(tappedIndex)
                    }
                }
            }
    ) {
        if (data.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val paddingBottom = 22.dp.toPx()
        val paddingTop = 12.dp.toPx()
        val chartHeight = height - paddingBottom - paddingTop

        // Horizontal Grid guidelines (similar to Recharts CartesianGrid)
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = paddingTop + (chartHeight / gridLines) * i
            drawLine(
                color = NeonSurfaceCardBorder.copy(alpha = 0.6f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )
        }

        val count = data.size
        val stepX = width / max(1, count - 1).toFloat()

        if (metric == ChartMetric.WEIGHT) {
            // Area + Line Chart (Recharts-style AreaChart)
            val validWeights = data.mapNotNull { it.weightKg }
            val minWeight = (validWeights.minOrNull() ?: 75.0) - 0.5
            val maxWeight = (validWeights.maxOrNull() ?: 80.0) + 0.5
            val range = max(0.1, maxWeight - minWeight)

            val points = mutableListOf<Offset>()
            data.forEachIndexed { i, pt ->
                val w = pt.weightKg ?: (validWeights.firstOrNull() ?: 77.0)
                val x = i * stepX
                val normalizedY = 1f - ((w - minWeight) / range).toFloat().coerceIn(0f, 1f)
                val y = paddingTop + (normalizedY * chartHeight * transitionProgress)
                points.add(Offset(x, y))
            }

            // Fill gradient path under curve
            if (points.isNotEmpty()) {
                val fillPath = Path().apply {
                    moveTo(points.first().x, height - paddingBottom)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, height - paddingBottom)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = 0.35f),
                            NeonCyan.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        startY = paddingTop,
                        endY = height - paddingBottom
                    )
                )

                // Smooth Spline Line
                val strokePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        val midX = (prev.x + curr.x) / 2
                        cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                    }
                }

                drawPath(
                    path = strokePath,
                    color = NeonCyan,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Highlight dots
                points.forEachIndexed { index, pt ->
                    val isSelected = selectedIndex == index
                    if (isSelected) {
                        // Halo glow
                        drawCircle(
                            color = NeonCyan.copy(alpha = 0.4f),
                            radius = 9.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = NeonCyan,
                            radius = 5.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = NeonBackground,
                            radius = 2.5.dp.toPx(),
                            center = pt
                        )

                        // Vertical guide cursor line
                        drawLine(
                            color = NeonCyan.copy(alpha = 0.5f),
                            start = Offset(pt.x, paddingTop),
                            end = Offset(pt.x, height - paddingBottom),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                        )
                    } else if (index == 0 || index == points.lastIndex || index % 7 == 0) {
                        drawCircle(
                            color = NeonCyan,
                            radius = 3.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }
        } else {
            // Bar Chart (Recharts-style BarChart for Volume Consistency)
            val maxVol = max(1000.0, data.maxOfOrNull { it.workoutVolumeKg } ?: 1000.0)
            val barWidth = max(4f, (width / count) * 0.65f)

            data.forEachIndexed { i, pt ->
                val x = (i * (width / count)) + ((width / count - barWidth) / 2f)
                val volFraction = (pt.workoutVolumeKg / maxVol).toFloat().coerceIn(0f, 1f)
                val barHeight = (volFraction * chartHeight * transitionProgress).coerceAtLeast(if (pt.hasWorkout) 6f else 2f)
                val y = (height - paddingBottom) - barHeight

                val isSelected = selectedIndex == i
                val barColor = if (pt.hasWorkout) {
                    if (isSelected) NeonCyan else NeonGreen
                } else {
                    Color(0xFF202636)
                }

                // Draw Bar
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            barColor,
                            barColor.copy(alpha = 0.6f)
                        ),
                        startY = y,
                        endY = height - paddingBottom
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                if (isSelected) {
                    // Top beacon indicator
                    drawCircle(
                        color = NeonCyan,
                        radius = 3.dp.toPx(),
                        center = Offset(x + barWidth / 2f, y - 6.dp.toPx())
                    )
                }
            }
        }

        // X-Axis Date Ticks
        val tickCount = min(5, count)
        val tickInterval = max(1, count / tickCount)
        for (i in 0 until count step tickInterval) {
            val pt = data[i]
            val x = if (metric == ChartMetric.WEIGHT) i * stepX else (i * (width / count)) + ((width / count) / 2f)
            drawLine(
                color = TextTertiary.copy(alpha = 0.5f),
                start = Offset(x, height - paddingBottom),
                end = Offset(x, height - paddingBottom + 4.dp.toPx()),
                strokeWidth = 1f
            )
        }
    }
}

@Composable
fun AddWeightModal(
    currentWeight: Double,
    onDismiss: () -> Unit,
    onConfirm: (weightKg: Double, note: String) -> Unit
) {
    var weightInput by remember { mutableStateOf(String.format(Locale.US, "%.1f", currentWeight)) }
    var noteInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_weight_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NeonSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                    listOf(NeonCyan, NeonMagenta)
                ),
                width = 1.5.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LOG BIOMETRIC WEIGHT",
                        color = NeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonCyan.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "+25 XP",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Body Weight (kg)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weight_modal_input"),
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

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Note (e.g. Morning fasting, Post-workout)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weight_modal_note_input"),
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

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("CANCEL")
                    }

                    Button(
                        onClick = {
                            val w = weightInput.toDoubleOrNull()
                            if (w != null && w > 0) {
                                onConfirm(w, noteInput)
                            }
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(46.dp)
                            .testTag("confirm_log_weight_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text(
                            text = "SAVE ENTRY",
                            color = NeonBackground,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * Computes discrete daily data points for the given days window (7, 14, or 30 days).
 */
private fun computeTrendData(
    days: Int,
    weightLogs: List<WeightLog>,
    workouts: List<WorkoutLog>
): List<DailyTrendPoint> {
    val now = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MMM d", Locale.US)
    val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val oneDayMs = 24 * 60 * 60 * 1000L

    val result = mutableListOf<DailyTrendPoint>()
    var lastKnownWeight = weightLogs.minByOrNull { it.timestamp }?.weightKg ?: 78.0

    for (i in (days - 1) downTo 0) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now.timeInMillis - (i * oneDayMs)
        }
        val dateString = dayFormat.format(cal.time)
        val displayDate = dateFormat.format(cal.time)
        val dayStart = cal.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val dayEnd = dayStart + oneDayMs

        // Find weight on or before this day
        val dayWeightLog = weightLogs.filter { it.timestamp in dayStart until dayEnd }.maxByOrNull { it.timestamp }
        if (dayWeightLog != null) {
            lastKnownWeight = dayWeightLog.weightKg
        }

        // Workouts on this day
        val dayWorkouts = workouts.filter { it.timestamp in dayStart until dayEnd }
        val dayVolume = dayWorkouts.sumOf { it.totalVolumeKg }

        result.add(
            DailyTrendPoint(
                dateLabel = displayDate,
                dayNumber = days - i,
                timestamp = dayStart,
                weightKg = lastKnownWeight,
                workoutVolumeKg = dayVolume,
                hasWorkout = dayWorkouts.isNotEmpty()
            )
        )
    }

    return result
}
