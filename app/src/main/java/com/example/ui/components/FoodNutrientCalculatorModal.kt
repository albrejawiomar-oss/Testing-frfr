package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CalculatedNutrients
import com.example.model.FoodItem
import com.example.ui.theme.*

@Composable
fun FoodNutrientCalculatorModal(
    foodItem: FoodItem,
    onDismiss: () -> Unit,
    onLogNutrients: (
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
    ) -> Unit
) {
    var servingInput by remember { mutableStateOf(foodItem.defaultServingG.toInt().toString()) }

    val servingG by remember {
        derivedStateOf { servingInput.toDoubleOrNull() ?: foodItem.defaultServingG }
    }

    val calculated by remember(servingG) {
        derivedStateOf { foodItem.calculateForServing(servingG) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.88f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .testTag("food_nutrient_calculator_modal"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NeonSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(NeonCyan, NeonMagenta)
                    ),
                    width = 1.5.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = foodItem.iconEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = foodItem.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${foodItem.category} • Barcode: ${foodItem.barcode.ifEmpty { "Manual" }}",
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NeonSurfaceCard)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Serving Size Adjuster
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NeonSurfaceCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(
                                listOf(NeonCyan.copy(alpha = 0.4f), Color.Transparent)
                            ),
                            width = 1.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SERVING SIZE (GRAMS)",
                                    color = TextTertiary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Nutrients adjust proportionally",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        val current = servingInput.toIntOrNull() ?: 100
                                        if (current > 10) servingInput = (current - 25).coerceAtLeast(10).toString()
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(NeonSurface)
                                        .border(1.dp, NeonSurfaceCardBorder, CircleShape)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "-", tint = NeonCyan)
                                }

                                OutlinedTextField(
                                    value = servingInput,
                                    onValueChange = { servingInput = it },
                                    modifier = Modifier
                                        .width(72.dp)
                                        .padding(horizontal = 4.dp),
                                    textStyle = LocalTextStyle.current.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )

                                IconButton(
                                    onClick = {
                                        val current = servingInput.toIntOrNull() ?: 100
                                        servingInput = (current + 25).toString()
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(NeonSurface)
                                        .border(1.dp, NeonSurfaceCardBorder, CircleShape)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "+", tint = NeonCyan)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Scrollable Nutrient Manifest: Every Single Nutrient
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Core Energy & Macronutrients
                        Text(
                            text = "CORE ENERGY & MACRONUTRIENTS",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NutrientBadge(
                                label = "Calories",
                                value = "${calculated.calories}",
                                unit = "kcal",
                                color = NeonGreen,
                                modifier = Modifier.weight(1f)
                            )
                            NutrientBadge(
                                label = "Protein",
                                value = "${calculated.proteinG}",
                                unit = "g",
                                color = NeonCyan,
                                modifier = Modifier.weight(1f)
                            )
                            NutrientBadge(
                                label = "Carbs",
                                value = "${calculated.carbsG}",
                                unit = "g",
                                color = NeonAmber,
                                modifier = Modifier.weight(1f)
                            )
                            NutrientBadge(
                                label = "Fats",
                                value = "${calculated.fatsG}",
                                unit = "g",
                                color = NeonMagenta,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Carbohydrate Breakdown & Fiber
                        Text(
                            text = "DIETARY FIBER & GLYCEMIC FRACTIONS",
                            color = NeonAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NutrientRow(
                                title = "Dietary Fiber",
                                amount = "${calculated.fiberG} g",
                                targetRda = "28g DV",
                                modifier = Modifier.weight(1f)
                            )
                            NutrientRow(
                                title = "Natural Sugars",
                                amount = "${calculated.sugarG} g",
                                targetRda = "<50g DV",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Electrolytes & Fluid Balance
                        Text(
                            text = "ELECTROLYTES & CELLULAR HYDRATION",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NutrientRow(
                                title = "Sodium (Na)",
                                amount = "${calculated.sodiumMg} mg",
                                targetRda = "2300mg DV",
                                modifier = Modifier.weight(1f)
                            )
                            NutrientRow(
                                title = "Potassium (K)",
                                amount = "${calculated.potassiumMg} mg",
                                targetRda = "4700mg DV",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Essential Minerals
                        Text(
                            text = "ESSENTIAL ANABOLIC MINERALS",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NutrientRow(
                                title = "Calcium (Ca)",
                                amount = "${calculated.calciumMg} mg",
                                targetRda = "1300mg DV",
                                modifier = Modifier.weight(1f)
                            )
                            NutrientRow(
                                title = "Iron (Fe)",
                                amount = "${calculated.ironMg} mg",
                                targetRda = "18mg DV",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NutrientRow(
                                title = "Magnesium (Mg)",
                                amount = "${calculated.magnesiumMg} mg",
                                targetRda = "420mg DV",
                                modifier = Modifier.weight(1f)
                            )
                            NutrientRow(
                                title = "Zinc (Zn)",
                                amount = "${calculated.zincMg} mg",
                                targetRda = "11mg DV",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Micronutrient Vitamins
                        Text(
                            text = "CELLULAR RECOVERY VITAMINS",
                            color = NeonMagenta,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NutrientRow(
                                title = "Vitamin C (Ascorbic)",
                                amount = "${calculated.vitaminCIu} mg",
                                targetRda = "90mg DV",
                                modifier = Modifier.weight(1f)
                            )
                            NutrientRow(
                                title = "Vitamin D (Cholecalciferol)",
                                amount = "${calculated.vitaminDIu} IU",
                                targetRda = "800IU DV",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Log Calculated Nutrients Button
                    Button(
                        onClick = {
                            onLogNutrients(
                                "${foodItem.name} (${servingG.toInt()}g)",
                                calculated.calories,
                                calculated.proteinG,
                                calculated.carbsG,
                                calculated.fatsG,
                                calculated.servingSizeG,
                                calculated.fiberG,
                                calculated.sugarG,
                                calculated.sodiumMg,
                                calculated.potassiumMg,
                                calculated.calciumMg,
                                calculated.ironMg,
                                calculated.vitaminCIu,
                                calculated.vitaminDIu,
                                calculated.magnesiumMg,
                                calculated.zincMg,
                                foodItem.barcode
                            )
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("log_calculated_nutrients_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "LOG CALCULATED FUEL (+20 XP)",
                            color = NeonBackground,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NutrientBadge(
    label: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(NeonSurfaceCard)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = color,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
            Text(
                text = unit,
                color = TextSecondary,
                fontSize = 9.sp
            )
            Text(
                text = label,
                color = TextTertiary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun NutrientRow(
    title: String,
    amount: String,
    targetRda: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NeonSurfaceCard)
            .border(1.dp, NeonSurfaceCardBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = targetRda,
                color = TextTertiary,
                fontSize = 9.sp
            )
        }

        Text(
            text = amount,
            color = NeonCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
