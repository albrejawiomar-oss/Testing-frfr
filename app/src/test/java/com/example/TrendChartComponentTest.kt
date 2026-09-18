package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.model.WeightLog
import com.example.model.WorkoutLog
import com.example.ui.components.TrendChartComponent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TrendChartComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTrendChartComponentRendersAndTogglesMetrics() {
        val now = System.currentTimeMillis()
        val weightLogs = listOf(
            WeightLog(id = "w1", weightKg = 79.5, timestamp = now - 86400000L * 10),
            WeightLog(id = "w2", weightKg = 77.2, timestamp = now)
        )
        val workoutLogs = listOf(
            WorkoutLog(
                id = "wo1",
                exerciseName = "Barbell Bench",
                muscleGroup = "Chest",
                weightKg = 80.0,
                sets = 4,
                reps = 10,
                totalVolumeKg = 3200.0,
                timestamp = now - 86400000L * 2
            )
        )

        composeTestRule.setContent {
            TrendChartComponent(
                weightLogs = weightLogs,
                workouts = workoutLogs,
                onLogWeight = { _, _ -> }
            )
        }

        // Verify visualization card is present
        composeTestRule.onNodeWithTag("trend_data_visualization_card").assertExists()

        // Toggle between metrics
        composeTestRule.onNodeWithTag("metric_pill_volume").performClick()
        composeTestRule.onNodeWithTag("metric_pill_weight").performClick()

        // Toggle time ranges
        composeTestRule.onNodeWithTag("range_pill_7D").performClick()
        composeTestRule.onNodeWithTag("range_pill_30D").performClick()
    }
}
