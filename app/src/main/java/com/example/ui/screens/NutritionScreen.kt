package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FoodDatabase
import com.example.model.FoodItem
import com.example.model.NutritionGoals
import com.example.model.NutritionLog
import com.example.model.UserProfile
import com.example.ui.components.BarcodeScannerModal
import com.example.ui.components.FoodNutrientCalculatorModal
import com.example.ui.components.HeaderComponent
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NutritionScreen(
    userProfile: UserProfile,
    nutritionLogs: List<NutritionLog>,
    goals: NutritionGoals,
    onLogMeal: (
        mealName: String,
        calories: Int,
        proteinG: Int,
        carbsG: Int,
        fatsG: Int,
        servingSizeG: Double,
        fiberG: Double,
        sugarG: Double,
        sodiumMg: Double,
        potassiumMg: Double,
        calciumMg: Double,
        ironMg: Double,
        vitaminCIu: Double,
        vitaminDIu: Double,
        magnesiumMg: Double,
        zincMg: Double,
        barcode: String
    ) -> Unit,
    onDeleteMeal: (logId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mealName by remember { mutableStateOf("Grilled Chicken Breast") }
    var caloriesInput by remember { mutableStateOf("248") }
    var proteinInput by remember { mutableStateOf("47") }
    var carbsInput by remember { mutableStateOf("0") }
    var fatsInput by remember { mutableStateOf("5") }

    // Advanced Micronutrient Inputs (Expandable)
    var showAdvancedNutrients by remember { mutableStateOf(false) }
    var servingSizeInput by remember { mutableStateOf("150.0") }
    var fiberInput by remember { mutableStateOf("0.0") }
    var sugarInput by remember { mutableStateOf("0.0") }
    var sodiumInput by remember { mutableStateOf("111.0") }
    var potassiumInput by remember { mutableStateOf("384.0") }
    var calciumInput by remember { mutableStateOf("22.5") }
    var ironInput by remember { mutableStateOf("1.5") }
    var vitCInput by remember { mutableStateOf("0.0") }
    var vitDInput by remember { mutableStateOf("7.5") }
    var magnesiumInput by remember { mutableStateOf("43.5") }
    var zincInput by remember { mutableStateOf("1.5") }

    // Barcode scanner & food calculator modals
    var showBarcodeScanner by remember { mutableStateOf(false) }
    var activeCalculatorFood by remember { mutableStateOf<FoodItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Aggregate totals for the day
    val totalCalories = remember(nutritionLogs) { nutritionLogs.sumOf { it.calories } }
    val totalProtein = remember(nutritionLogs) { nutritionLogs.sumOf { it.proteinG } }
    val totalCarbs = remember(nutritionLogs) { nutritionLogs.sumOf { it.carbsG } }
    val totalFats = remember(nutritionLogs) { nutritionLogs.sumOf { it.fatsG } }
    val totalFiber = remember(nutritionLogs) { nutritionLogs.sumOf { it.fiberG } }
    val totalSodium = remember(nutritionLogs) { nutritionLogs.sumOf { it.sodiumMg } }
    val totalPotassium = remember(nutritionLogs) { nutritionLogs.sumOf { it.potassiumMg } }
    val totalCalcium = remember(nutritionLogs) { nutritionLogs.sumOf { it.calciumMg } }
    val totalIron = remember(nutritionLogs) { nutritionLogs.sumOf { it.ironMg } }
    val totalVitC = remember(nutritionLogs) { nutritionLogs.sumOf { it.vitaminCIu } }
    val totalVitD = remember(nutritionLogs) { nutritionLogs.sumOf { it.vitaminDIu } }

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

        // Section Title & Barcode Scanner Quick Launch Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CELLULAR NUTRITION MATRIX",
                        color = NeonMagenta,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Full-spectrum macro & micronutrient calculation engine",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { showBarcodeScanner = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("open_barcode_scanner_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scan",
                        tint = NeonBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SCAN",
                        color = NeonBackground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Target Goals Visual Progress Cards
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("macro_goals_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NeonSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(NeonMagenta.copy(alpha = 0.6f), NeonSurfaceCardBorder)
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
                            text = "DAILY TARGETS & CELLULAR BALANCE",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${nutritionLogs.size} logs today",
                            color = NeonAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calories Bar
                    DetailedMacroProgress(
                        name = "ENERGY (CALORIES)",
                        current = totalCalories,
                        target = goals.targetCalories,
                        unit = "kcal",
                        color = NeonGreen
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Protein Bar
                    DetailedMacroProgress(
                        name = "PROTEIN SYNTHESIS",
                        current = totalProtein,
                        target = goals.targetProtein,
                        unit = "g",
                        color = NeonCyan
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Carbs Bar
                    DetailedMacroProgress(
                        name = "CARBOHYDRATES",
                        current = totalCarbs,
                        target = goals.targetCarbs,
                        unit = "g",
                        color = NeonAmber
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fats Bar
                    DetailedMacroProgress(
                        name = "ESSENTIAL FATS",
                        current = totalFats,
                        target = goals.targetFats,
                        unit = "g",
                        color = NeonMagenta
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Micronutrient Summary Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MicroChip(title = "Fiber", value = String.format("%.1fg", totalFiber), color = NeonAmber, modifier = Modifier.weight(1f))
                        MicroChip(title = "Sodium", value = String.format("%.0fmg", totalSodium), color = NeonGreen, modifier = Modifier.weight(1f))
                        MicroChip(title = "Potassium", value = String.format("%.0fmg", totalPotassium), color = NeonCyan, modifier = Modifier.weight(1f))
                        MicroChip(title = "Vit D", value = String.format("%.0fIU", totalVitD), color = NeonMagenta, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Search Food Database & Instant Barcode Lookup
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("food_catalog_search_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NeonSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(NeonCyan.copy(alpha = 0.5f), NeonMagenta.copy(alpha = 0.5f))
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
                            text = "FOOD DATABASE & CALCULATOR",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Calculate any food →",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search food or enter barcode...", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("food_search_input"),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonCyan)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showBarcodeScanner = true }) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = NeonCyan)
                            }
                        },
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filtered List of Foods (Tap to open Nutrient Calculator)
                    val matchingFoods = remember(searchQuery) {
                        FoodDatabase.search(searchQuery).take(4)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        matchingFoods.forEach { food ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NeonSurfaceCard)
                                    .border(1.dp, NeonSurfaceCardBorder, RoundedCornerShape(12.dp))
                                    .clickable { activeCalculatorFood = food }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = food.iconEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = food.name,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${food.caloriesPer100g.toInt()} kcal • ${food.proteinPer100g}g P • ${food.carbsPer100g}g C • ${food.fatsPer100g}g F (per 100g)",
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "CALCULATE 🧪",
                                    color = NeonCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick-Add Meal Form Card with Complete Nutrient Breakdown
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_add_meal_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NeonSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(NeonMagenta.copy(alpha = 0.5f), NeonSurfaceCardBorder)
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
                            text = "CUSTOM MEAL & NUTRIENT LOG",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )

                        TextButton(onClick = { showAdvancedNutrients = !showAdvancedNutrients }) {
                            Text(
                                text = if (showAdvancedNutrients) "Hide Micros ▲" else "+ All Nutrients ▼",
                                color = NeonMagenta,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Meal Name
                    OutlinedTextField(
                        value = mealName,
                        onValueChange = { mealName = it },
                        label = { Text("Meal / Food Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("meal_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonMagenta,
                            unfocusedBorderColor = NeonSurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = NeonSurfaceCard,
                            unfocusedContainerColor = NeonSurfaceCard
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4-column primary inputs: Calories, Protein, Carbs, Fats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = caloriesInput,
                            onValueChange = { caloriesInput = it },
                            label = { Text("Kcal") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("meal_calories_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = NeonSurfaceCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = NeonSurfaceCard,
                                unfocusedContainerColor = NeonSurfaceCard
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = proteinInput,
                            onValueChange = { proteinInput = it },
                            label = { Text("Prot (g)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("meal_protein_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = NeonSurfaceCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = NeonSurfaceCard,
                                unfocusedContainerColor = NeonSurfaceCard
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = carbsInput,
                            onValueChange = { carbsInput = it },
                            label = { Text("Carb (g)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonAmber,
                                unfocusedBorderColor = NeonSurfaceCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = NeonSurfaceCard,
                                unfocusedContainerColor = NeonSurfaceCard
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = fatsInput,
                            onValueChange = { fatsInput = it },
                            label = { Text("Fat (g)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonMagenta,
                                unfocusedBorderColor = NeonSurfaceCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = NeonSurfaceCard,
                                unfocusedContainerColor = NeonSurfaceCard
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    // Advanced Micronutrient Fields
                    AnimatedVisibility(visible = showAdvancedNutrients) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "DETAILED MICRONUTRIENTS (VITAMINS & MINERALS)",
                                color = NeonAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MiniNutrientInput("Fiber (g)", fiberInput, { fiberInput = it }, Modifier.weight(1f))
                                MiniNutrientInput("Sugar (g)", sugarInput, { sugarInput = it }, Modifier.weight(1f))
                                MiniNutrientInput("Sodium (mg)", sodiumInput, { sodiumInput = it }, Modifier.weight(1f))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MiniNutrientInput("Potassium (mg)", potassiumInput, { potassiumInput = it }, Modifier.weight(1f))
                                MiniNutrientInput("Calcium (mg)", calciumInput, { calciumInput = it }, Modifier.weight(1f))
                                MiniNutrientInput("Iron (mg)", ironInput, { ironInput = it }, Modifier.weight(1f))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MiniNutrientInput("Vit C (mg)", vitCInput, { vitCInput = it }, Modifier.weight(1f))
                                MiniNutrientInput("Vit D (IU)", vitDInput, { vitDInput = it }, Modifier.weight(1f))
                                MiniNutrientInput("Magnesium (mg)", magnesiumInput, { magnesiumInput = it }, Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Log Meal Button (+20 XP)
                    Button(
                        onClick = {
                            val cal = caloriesInput.toIntOrNull() ?: 0
                            val p = proteinInput.toIntOrNull() ?: 0
                            val c = carbsInput.toIntOrNull() ?: 0
                            val f = fatsInput.toIntOrNull() ?: 0
                            val serving = servingSizeInput.toDoubleOrNull() ?: 100.0
                            val fiber = fiberInput.toDoubleOrNull() ?: 0.0
                            val sugar = sugarInput.toDoubleOrNull() ?: 0.0
                            val sodium = sodiumInput.toDoubleOrNull() ?: 0.0
                            val potassium = potassiumInput.toDoubleOrNull() ?: 0.0
                            val calcium = calciumInput.toDoubleOrNull() ?: 0.0
                            val iron = ironInput.toDoubleOrNull() ?: 0.0
                            val vitC = vitCInput.toDoubleOrNull() ?: 0.0
                            val vitD = vitDInput.toDoubleOrNull() ?: 0.0
                            val magnesium = magnesiumInput.toDoubleOrNull() ?: 0.0
                            val zinc = zincInput.toDoubleOrNull() ?: 0.0

                            if (mealName.isNotBlank() && (cal > 0 || p > 0)) {
                                onLogMeal(
                                    mealName,
                                    cal,
                                    p,
                                    c,
                                    f,
                                    serving,
                                    fiber,
                                    sugar,
                                    sodium,
                                    potassium,
                                    calcium,
                                    iron,
                                    vitC,
                                    vitD,
                                    magnesium,
                                    zinc,
                                    ""
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_log_meal_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = NeonBackground,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LOG COMPREHENSIVE MEAL (+20 XP)",
                            color = NeonBackground,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // Today's Meals History List
        item {
            Text(
                text = "LOGGED MEALS TODAY (${nutritionLogs.size})",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        if (nutritionLogs.isEmpty()) {
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
                        text = "No fuel logs entered yet. Scan a barcode or calculate nutrients above!",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(nutritionLogs, key = { it.id }) { log ->
                ComprehensiveNutritionLogItem(
                    log = log,
                    timeFormatted = timeFormat.format(Date(log.timestamp)),
                    onDelete = { onDeleteMeal(log.id) }
                )
            }
        }
    }

    // Barcode Scanner Modal
    if (showBarcodeScanner) {
        BarcodeScannerModal(
            onDismiss = { showBarcodeScanner = false },
            onBarcodeScanned = { scannedFood ->
                showBarcodeScanner = false
                activeCalculatorFood = scannedFood
            }
        )
    }

    // Food Nutrient Calculator Modal
    activeCalculatorFood?.let { food ->
        FoodNutrientCalculatorModal(
            foodItem = food,
            onDismiss = { activeCalculatorFood = null },
            onLogNutrients = { name, cal, p, c, f, serving, fiber, sugar, sod, pot, calc, iron, vc, vd, mg, zn, bc ->
                onLogMeal(name, cal, p, c, f, serving, fiber, sugar, sod, pot, calc, iron, vc, vd, mg, zn, bc)
            }
        )
    }
}

@Composable
private fun MiniNutrientInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 9.sp) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, color = TextPrimary),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = NeonSurfaceCardBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = NeonSurfaceCard,
            unfocusedContainerColor = NeonSurfaceCard
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
fun DetailedMacroProgress(
    name: String,
    current: Int,
    target: Int,
    unit: String,
    color: Color
) {
    val progress = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val percentage = ((current.toFloat() / target.toFloat()) * 100).toInt()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "$current / $target $unit ($percentage%)",
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1E2232))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun MicroChip(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NeonSurfaceCard)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = TextTertiary, fontSize = 9.sp)
            Text(text = value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ComprehensiveNutritionLogItem(
    log: NutritionLog,
    timeFormatted: String,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("nutrition_log_item_${log.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NeonSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(NeonSurfaceCardBorder, NeonMagenta.copy(alpha = 0.3f))
            ),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.mealName,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "${log.calories} kcal",
                            color = NeonGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                        Text(text = "•", color = TextTertiary, fontSize = 11.sp)
                        Text(
                            text = "${log.proteinG}g Protein",
                            color = NeonCyan,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                        Text(text = "•", color = TextTertiary, fontSize = 11.sp)
                        Text(
                            text = "${log.carbsG}g C / ${log.fatsG}g F",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = "$timeFormatted ${if (log.barcode.isNotEmpty()) "• Barcode: ${log.barcode}" else ""}",
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Meal",
                            tint = DangerNeon,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Expandable full-spectrum micronutrient breakdown
            if (expanded || log.fiberG > 0 || log.sodiumMg > 0 || log.potassiumMg > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = NeonSurfaceCardBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Fiber: ${log.fiberG}g",
                        color = NeonAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "•", color = TextTertiary, fontSize = 10.sp)
                    Text(
                        text = "Na: ${log.sodiumMg.toInt()}mg",
                        color = NeonGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "•", color = TextTertiary, fontSize = 10.sp)
                    Text(
                        text = "K: ${log.potassiumMg.toInt()}mg",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "•", color = TextTertiary, fontSize = 10.sp)
                    Text(
                        text = "Ca: ${log.calciumMg.toInt()}mg",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "•", color = TextTertiary, fontSize = 10.sp)
                    Text(
                        text = "Fe: ${log.ironMg}mg",
                        color = NeonMagenta,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
