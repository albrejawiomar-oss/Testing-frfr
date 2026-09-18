package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyRoutine
import com.example.ui.theme.*

@Composable
fun DailyRoutineSection(
    routines: List<DailyRoutine>,
    onToggleRoutine: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_routine_section"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NeonSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(NeonGreen.copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.3f))
            ),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NeonGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DAILY HABIT PROTOCOLS",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                val completedCount = routines.count { it.isCompleted }
                Text(
                    text = "$completedCount / ${routines.size} Complete",
                    color = NeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                routines.forEach { routine ->
                    DailyRoutineItem(
                        routine = routine,
                        onToggle = { onToggleRoutine(routine.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyRoutineItem(
    routine: DailyRoutine,
    onToggle: () -> Unit
) {
    val attrColor = when (routine.attributeType) {
        "STR" -> NeonCyan
        "AGI" -> NeonGreen
        "END" -> NeonMagenta
        else -> NeonAmber
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (routine.isCompleted) NeonGreen.copy(alpha = 0.08f) else NeonSurfaceCard)
            .border(
                1.dp,
                if (routine.isCompleted) NeonGreen.copy(alpha = 0.4f) else NeonSurfaceCardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onToggle() }
            .padding(12.dp)
            .testTag("routine_item_${routine.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Custom Styled Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (routine.isCompleted) NeonGreen else Color.Transparent)
                .border(1.5.dp, if (routine.isCompleted) NeonGreen else TextTertiary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (routine.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = NeonBackground,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = routine.title,
                color = if (routine.isCompleted) TextTertiary else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "+${routine.xpReward} XP",
                    color = NeonAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• +${routine.attributeReward} ${routine.attributeType}",
                    color = attrColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
