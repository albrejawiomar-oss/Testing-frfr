package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class WizardStep {
    DIETARY_QUESTIONS,
    DEFICIENCY_QUESTIONS,
    SYNTHESIZING,
    PLAN_OVERVIEW
}

@Composable
fun DietaryAssessmentModal(
    existingPlan: DietaryPlan?,
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onSavePlan: (DietaryPlan) -> Unit,
    onApplyPlanToTracker: (DietaryPlan) -> Unit,
    onOpenProModal: () -> Unit
) {
    var currentStep by remember {
        mutableStateOf(if (existingPlan != null) WizardStep.PLAN_OVERVIEW else WizardStep.DIETARY_QUESTIONS)
    }

    // Step 1 State: Dietary Questions
    var selectedDietType by remember {
        mutableStateOf(existingPlan?.dietType ?: DietType.OMNIVORE)
    }
    var selectedGoal by remember {
        mutableStateOf(existingPlan?.goal ?: FitnessDietGoal.LEAN_HYPERTROPHY)
    }
    var selectedFrequency by remember {
        mutableStateOf(existingPlan?.mealFrequency ?: MealFrequency.THREE_MEALS)
    }
    var selectedAllergies by remember {
        mutableStateOf(existingPlan?.allergies?.toSet() ?: setOf("None"))
    }
    var selectedWaterRating by remember {
        mutableStateOf(existingPlan?.waterIntakeRating ?: "2.5L / Optimal Hydration")
    }

    // Step 2 State: Deficiency Questions
    var selectedDeficiencies by remember {
        val initialDef = existingPlan?.deficiencies?.map { it.deficiency }?.toSet()
            ?: setOf(NutrientDeficiency.VITAMIN_D3, NutrientDeficiency.MAGNESIUM)
        mutableStateOf(initialDef)
    }

    // Generated plan state
    var activePlan by remember { mutableStateOf(existingPlan) }
    var planAppliedFeedback by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)
                .testTag("dietary_assessment_modal"),
            shape = RoundedCornerShape(24.dp),
            color = NeonBackground,
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(NeonMagenta.copy(alpha = 0.8f), NeonCyan.copy(alpha = 0.5f))
                ),
                width = 1.5.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonMagenta.copy(alpha = 0.2f))
                                .border(1.dp, NeonMagenta, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (currentStep) {
                                    WizardStep.DIETARY_QUESTIONS -> "🥗"
                                    WizardStep.DEFICIENCY_QUESTIONS -> "🧬"
                                    WizardStep.SYNTHESIZING -> "⚡"
                                    WizardStep.PLAN_OVERVIEW -> "📋"
                                },
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = when (currentStep) {
                                    WizardStep.DIETARY_QUESTIONS -> "DIETARY ASSESSMENT (1/2)"
                                    WizardStep.DEFICIENCY_QUESTIONS -> "DEFICIENCY MATRIX (2/2)"
                                    WizardStep.SYNTHESIZING -> "SYNTHESIZING PROTOCOL"
                                    WizardStep.PLAN_OVERVIEW -> "CELLULAR DIET PLAN"
                                },
                                color = NeonMagenta,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = when (currentStep) {
                                    WizardStep.DIETARY_QUESTIONS -> "Nutrition Habits & Metabolic Targets"
                                    WizardStep.DEFICIENCY_QUESTIONS -> "Biomarker Symptoms & Deficiencies"
                                    WizardStep.SYNTHESIZING -> "Computing optimal macronutrient split..."
                                    WizardStep.PLAN_OVERVIEW -> if (activePlan?.isProUnlocked == true || userProfile.isProUnlocked) "Pro Full-Spectrum Protocol Active" else "Freemium Protocol Synthesized"
                                },
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_dietary_modal_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = NeonSurfaceCardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Body based on step
                Box(modifier = Modifier.weight(1f)) {
                    when (currentStep) {
                        WizardStep.DIETARY_QUESTIONS -> {
                            DietaryQuestionsStep(
                                selectedDietType = selectedDietType,
                                onSelectDietType = { selectedDietType = it },
                                selectedGoal = selectedGoal,
                                onSelectGoal = { selectedGoal = it },
                                selectedFrequency = selectedFrequency,
                                onSelectFrequency = { selectedFrequency = it },
                                selectedAllergies = selectedAllergies,
                                onToggleAllergy = { allergy ->
                                    selectedAllergies = if (allergy == "None") {
                                        setOf("None")
                                    } else {
                                        val next = (selectedAllergies - "None").toMutableSet()
                                        if (next.contains(allergy)) next.remove(allergy) else next.add(allergy)
                                        if (next.isEmpty()) setOf("None") else next
                                    }
                                },
                                selectedWaterRating = selectedWaterRating,
                                onSelectWaterRating = { selectedWaterRating = it },
                                onNext = {
                                    currentStep = WizardStep.DEFICIENCY_QUESTIONS
                                }
                            )
                        }

                        WizardStep.DEFICIENCY_QUESTIONS -> {
                            DeficiencyQuestionsStep(
                                selectedDeficiencies = selectedDeficiencies,
                                onToggleDeficiency = { def ->
                                    val next = selectedDeficiencies.toMutableSet()
                                    if (next.contains(def)) next.remove(def) else next.add(def)
                                    selectedDeficiencies = next
                                },
                                onBack = { currentStep = WizardStep.DIETARY_QUESTIONS },
                                onGeneratePlan = {
                                    currentStep = WizardStep.SYNTHESIZING
                                    coroutineScope.launch {
                                        delay(1100) // Realistic synthesizing calculation
                                        val newPlan = DietaryPlanGenerator.generatePlan(
                                            dietType = selectedDietType,
                                            goal = selectedGoal,
                                            mealFrequency = selectedFrequency,
                                            allergies = selectedAllergies.toList(),
                                            waterIntakeRating = selectedWaterRating,
                                            selectedDeficiencies = selectedDeficiencies.toList(),
                                            userWeightKg = 76.0,
                                            isPro = userProfile.isProUnlocked
                                        )
                                        activePlan = newPlan
                                        onSavePlan(newPlan)
                                        currentStep = WizardStep.PLAN_OVERVIEW
                                    }
                                }
                            )
                        }

                        WizardStep.SYNTHESIZING -> {
                            SynthesizingStep()
                        }

                        WizardStep.PLAN_OVERVIEW -> {
                            activePlan?.let { plan ->
                                PlanOverviewStep(
                                    plan = plan,
                                    userProfile = userProfile,
                                    onRetake = {
                                        currentStep = WizardStep.DIETARY_QUESTIONS
                                    },
                                    onApplyToTracker = {
                                        onApplyPlanToTracker(plan)
                                        planAppliedFeedback = true
                                    },
                                    onOpenProModal = onOpenProModal,
                                    planAppliedFeedback = planAppliedFeedback
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DietaryQuestionsStep(
    selectedDietType: DietType,
    onSelectDietType: (DietType) -> Unit,
    selectedGoal: FitnessDietGoal,
    onSelectGoal: (FitnessDietGoal) -> Unit,
    selectedFrequency: MealFrequency,
    onSelectFrequency: (MealFrequency) -> Unit,
    selectedAllergies: Set<String>,
    onToggleAllergy: (String) -> Unit,
    selectedWaterRating: String,
    onSelectWaterRating: (String) -> Unit,
    onNext: () -> Unit
) {
    val allergyOptions = listOf("None", "Dairy / Lactose", "Gluten / Wheat", "Peanuts & Tree Nuts", "Shellfish", "Eggs", "Soy")
    val waterOptions = listOf("Under 1.5L (Low)", "2.0L (Moderate)", "2.5L / Optimal Hydration", "3.5L+ (Heavy Training)")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Question 1: Diet Type
        item {
            Column {
                Text(
                    text = "1. What is your primary dietary structure?",
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Select your current dietary preferences or lifestyle framework:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DietType.values().forEach { diet ->
                        val isSelected = diet == selectedDietType
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectDietType(diet) }
                                .testTag("diet_option_${diet.name}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) NeonSurfaceCard else NeonSurface
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(
                                    if (isSelected) listOf(NeonCyan, NeonMagenta) else listOf(NeonSurfaceCardBorder, NeonSurfaceCardBorder)
                                ),
                                width = if (isSelected) 1.5.dp else 1.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = diet.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = diet.title,
                                        color = if (isSelected) NeonCyan else TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = diet.subtitle,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Question 2: Fitness / Caloric Goal
        item {
            Column {
                Text(
                    text = "2. What is your primary fitness & metabolic goal?",
                    color = NeonAmber,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Caloric intake and protein ratios will calibrate to this directive:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FitnessDietGoal.values().forEach { goal ->
                        val isSelected = goal == selectedGoal
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectGoal(goal) }
                                .testTag("goal_option_${goal.name}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) NeonSurfaceCard else NeonSurface
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(
                                    if (isSelected) listOf(NeonAmber, NeonGreen) else listOf(NeonSurfaceCardBorder, NeonSurfaceCardBorder)
                                ),
                                width = if (isSelected) 1.5.dp else 1.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = goal.title,
                                            color = if (isSelected) NeonAmber else TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(NeonAmber.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = goal.badge,
                                                color = NeonAmber,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = goal.description,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = NeonAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Question 3: Meal Cadence
        item {
            Column {
                Text(
                    text = "3. Preferred daily meal distribution",
                    color = NeonGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MealFrequency.values().forEach { freq ->
                        val isSelected = freq == selectedFrequency
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonGreen.copy(alpha = 0.2f) else NeonSurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonGreen else NeonSurfaceCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectFrequency(freq) }
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = freq.title,
                                    color = if (isSelected) NeonGreen else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = freq.desc,
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Question 4: Allergies & Sensitivities
        item {
            Column {
                Text(
                    text = "4. Food intolerances or allergies",
                    color = NeonMagenta,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allergyOptions.forEach { allergy ->
                        val isSelected = selectedAllergies.contains(allergy)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onToggleAllergy(allergy) },
                            label = { Text(allergy, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonMagenta,
                                selectedLabelColor = NeonBackground,
                                containerColor = NeonSurfaceCard,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Question 5: Water Hydration
        item {
            Column {
                Text(
                    text = "5. Daily water intake habit",
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    waterOptions.take(2).forEach { water ->
                        val isSelected = selectedWaterRating == water
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectWaterRating(water) },
                            label = { Text(water, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    waterOptions.drop(2).forEach { water ->
                        val isSelected = selectedWaterRating == water
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectWaterRating(water) },
                            label = { Text(water, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Proceed Button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("diet_questions_next_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "NEXT: ASSESS DEFICIENCIES →",
                    color = NeonBackground,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun DeficiencyQuestionsStep(
    selectedDeficiencies: Set<NutrientDeficiency>,
    onToggleDeficiency: (NutrientDeficiency) -> Unit,
    onBack: () -> Unit,
    onGeneratePlan: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NeonSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(NeonMagenta.copy(alpha = 0.6f), NeonAmber.copy(alpha = 0.4f))
                    ),
                    width = 1.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🧬", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SYMPTOM & DEFICIENCY CHECKLIST",
                            color = NeonMagenta,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Select any symptoms you experience or confirmed nutrient gaps. Our engine computes tailored remediation dosages, bioavailable food sources, and timing.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Common Micronutrient Flags (${selectedDeficiencies.size} selected)",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                if (selectedDeficiencies.isNotEmpty()) {
                    Text(
                        text = "Remedies calculated",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(NutrientDeficiency.values().toList(), key = { it.id }) { def ->
            val isSelected = selectedDeficiencies.contains(def)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleDeficiency(def) }
                    .testTag("deficiency_item_${def.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) NeonSurfaceCard else NeonSurface
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        if (isSelected) listOf(NeonMagenta, NeonCyan) else listOf(NeonSurfaceCardBorder, NeonSurfaceCardBorder)
                    ),
                    width = if (isSelected) 1.5.dp else 1.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Emoji Icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) NeonMagenta.copy(alpha = 0.2f) else NeonSurface)
                            .border(1.dp, if (isSelected) NeonMagenta else NeonSurfaceCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = def.icon, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = def.nutrientName,
                            color = if (isSelected) NeonMagenta else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Symptoms: ${def.keySymptoms}",
                            color = TextSecondary,
                            fontSize = 10.5.sp,
                            lineHeight = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Target dose: ${def.therapeuticDose}",
                            color = NeonAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleDeficiency(def) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = NeonMagenta,
                            checkmarkColor = NeonBackground,
                            uncheckedColor = TextTertiary
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("← BACK", color = TextSecondary, fontSize = 12.sp)
                }

                Button(
                    onClick = onGeneratePlan,
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp)
                        .testTag("generate_dietary_plan_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "SYNTHESIZE PLAN ⚡",
                        color = NeonBackground,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SynthesizingStep() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = NeonMagenta,
            trackColor = NeonSurfaceCard,
            strokeWidth = 4.dp,
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "SYNTHESIZING CELLULAR BLUEPRINT...",
            color = NeonMagenta,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Calibrating macro thresholds, bioavailability co-factors, and personalized deficiency remediation matrices...",
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun PlanOverviewStep(
    plan: DietaryPlan,
    userProfile: UserProfile,
    onRetake: () -> Unit,
    onApplyToTracker: () -> Unit,
    onOpenProModal: () -> Unit,
    planAppliedFeedback: Boolean
) {
    val isPro = plan.isProUnlocked || userProfile.isProUnlocked

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Top Banner: Freemium Pro Status
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("freemium_tier_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPro) Color(0xFF0C241B) else NeonSurfaceCard
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        if (isPro) listOf(NeonGreen, Color(0xFF00FF88)) else listOf(NeonAmber, NeonMagenta)
                    ),
                    width = 1.5.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = if (isPro) "👑" else "⚡", fontSize = 26.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isPro) "PRO PLAN ACTIVATED (UNLOCKED)" else "FREEMIUM PLAN TIER",
                                color = if (isPro) NeonGreen else NeonAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (isPro)
                                    "All deficiency bio-protocols, 4-tier daily meals & auto-sync unlocked."
                                else
                                    "Free baseline targets unlocked. Pro unlocks all deficiency blueprints & tracker auto-sync.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    if (!isPro) {
                        Button(
                            onClick = onOpenProModal,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("upgrade_plan_pro_btn")
                        ) {
                            Text("UPGRADE", color = NeonBackground, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Caloric & Macro Calculation Dashboard
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("plan_targets_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NeonSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(NeonCyan.copy(alpha = 0.5f), NeonSurfaceCardBorder)
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
                        Column {
                            Text(
                                text = "CALCULATED CELLULAR TARGETS",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${plan.dietType.title} • ${plan.goal.title}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${plan.targetCalories} kcal",
                                color = NeonGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MacroPlanBox(
                            title = "PROTEIN",
                            amount = "${plan.targetProtein}g",
                            kcal = "${plan.targetProtein * 4} kcal",
                            color = NeonCyan,
                            modifier = Modifier.weight(1f)
                        )
                        MacroPlanBox(
                            title = "CARBS",
                            amount = "${plan.targetCarbs}g",
                            kcal = "${plan.targetCarbs * 4} kcal",
                            color = NeonAmber,
                            modifier = Modifier.weight(1f)
                        )
                        MacroPlanBox(
                            title = "FATS",
                            amount = "${plan.targetFats}g",
                            kcal = "${plan.targetFats * 9} kcal",
                            color = NeonMagenta,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Action: Apply to tracker
                    if (planAppliedFeedback) {
                        Text(
                            text = "✓ Targets successfully applied to your daily Nutrition tracker & Daily Routines!",
                            color = NeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Button(
                            onClick = {
                                if (!isPro) {
                                    onOpenProModal()
                                } else {
                                    onApplyToTracker()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPro) NeonGreen else NeonSurfaceCard
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("apply_plan_to_tracker_btn")
                        ) {
                            Icon(
                                imageVector = if (isPro) Icons.Default.Check else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isPro) NeonBackground else NeonAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPro) "APPLY TARGETS & REMEDIES TO ACTIVE TRACKER" else "APPLY TARGETS TO TRACKER (PRO)",
                                color = if (isPro) NeonBackground else NeonAmber,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Targeted Deficiency Remediation Blueprints
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DEFICIENCY REMEDIATION PROTOCOLS",
                        color = NeonMagenta,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${plan.deficiencies.size} Detected",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = "Specific therapeutic dosages, synergistic co-factors, and whole foods tailored to your diet:",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        items(plan.deficiencies) { remedy ->
            val isGated = remedy.isProExclusive && !isPro

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isGated) onOpenProModal()
                    }
                    .testTag("remedy_card_${remedy.deficiency.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isGated) NeonSurfaceCard.copy(alpha = 0.6f) else NeonSurfaceCard
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        if (isGated) listOf(NeonSurfaceCardBorder, NeonSurfaceCardBorder) else listOf(NeonMagenta.copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.3f))
                    ),
                    width = 1.dp
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = remedy.deficiency.icon, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = remedy.deficiency.nutrientName,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (isGated) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonAmber.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Pro Locked",
                                        tint = NeonAmber,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PRO PROTOCOL", color = NeonAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Text(
                                text = "FREE ACCESS",
                                color = NeonGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isGated) {
                        Text(
                            text = "🔒 Unlock Pro to view therapeutic dosage, timing co-factors, and food lists for ${remedy.deficiency.nutrientName}.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    } else {
                        // Therapeutic Dosage & Timing
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Target Dosage", color = TextTertiary, fontSize = 10.sp)
                                Text(text = remedy.targetedDose, color = NeonAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Optimal Timing", color = TextTertiary, fontSize = 10.sp)
                                Text(text = remedy.timingAdvice, color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Synergy Guidance
                        Text(
                            text = "💡 Bioavailability synergy: ${remedy.lifestyleGuidance}",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Recommended Whole Foods
                        Text(
                            text = "Top Whole Foods: ${remedy.recommendedFoods.joinToString(", ")}",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Section 3: Curated Daily Meals
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CUSTOMIZED DAILY MEAL STRUCTURE",
                        color = NeonAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "4-Meal Split",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = "Meals matched specifically to your ${plan.dietType.title} and nutrient deficiency gaps:",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        items(plan.meals) { meal ->
            val isMealGated = meal.isProExclusive && !isPro

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isMealGated) onOpenProModal()
                    }
                    .testTag("planned_meal_${meal.mealType}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isMealGated) NeonSurfaceCard.copy(alpha = 0.6f) else NeonSurfaceCard
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        if (isMealGated) listOf(NeonSurfaceCardBorder, NeonSurfaceCardBorder) else listOf(NeonAmber.copy(alpha = 0.5f), NeonMagenta.copy(alpha = 0.3f))
                    ),
                    width = 1.dp
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = meal.mealType.uppercase(),
                            color = NeonAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        if (isMealGated) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonAmber.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = NeonAmber,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PRO RECIPE", color = NeonAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Text(
                                text = "${meal.calories} kcal • ${meal.proteinG}g P",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (isMealGated) {
                        Text(
                            text = "🔒 ${meal.mealType} Blueprint & Micronutrient Boost",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Unlock Pro to view recipe, ingredient portions, and macro allocations for this meal.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    } else {
                        Text(
                            text = meal.name,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = meal.description,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Targeted boosts chips
                        if (meal.targetedDeficiencyBoosts.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                meal.targetedDeficiencyBoosts.forEach { boost ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NeonMagenta.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "+$boost",
                                            color = NeonMagenta,
                                            fontSize = 9.sp,
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

        // Section 4: Daily Habits Ready to Sync
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NeonSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NeonGreen.copy(alpha = 0.6f), NeonCyan.copy(alpha = 0.3f))),
                    width = 1.dp
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "AUTOMATED ROUTINE HABITS",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "These habits can be automatically loaded into your daily RPG routine quests:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    plan.dailyHabitsToAdd.forEach { habit ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚡", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = habit, color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Bottom Action Row
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRetake,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("RETAKE QUIZ", color = TextSecondary, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        if (!isPro) onOpenProModal() else onApplyToTracker()
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp)
                        .testTag("final_apply_plan_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPro) NeonGreen else NeonAmber
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isPro) "APPLY & SYNC PROTOCOL" else "UNLOCK FULL PROTOCOL ($12)",
                        color = NeonBackground,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroPlanBox(
    title: String,
    amount: String,
    kcal: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = title, color = color, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = amount, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = kcal, color = TextSecondary, fontSize = 9.sp)
        }
    }
}
