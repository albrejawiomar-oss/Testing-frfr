package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.FoodDatabase
import com.example.model.FoodItem
import com.example.ui.theme.*

@Composable
fun BarcodeScannerModal(
    onDismiss: () -> Unit,
    onBarcodeScanned: (FoodItem) -> Unit
) {
    var manualBarcode by remember { mutableStateOf("") }
    var scanError by remember { mutableStateOf<String?>(null) }
    var showQuickList by remember { mutableStateOf(false) }

    // Laser Animation line
    val infiniteTransition = rememberInfiniteTransition(label = "laser_scanner")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_position"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(NeonSurface)
                    .border(1.5.dp, NeonCyan, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanner",
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NEURAL BARCODE SCANNER",
                            color = NeonCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
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

                Spacer(modifier = Modifier.height(16.dp))

                // Viewfinder Reticle / Simulated Camera Viewport
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF070B14))
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .testTag("barcode_viewfinder"),
                    contentAlignment = Alignment.Center
                ) {
                    // Reticle Corner Brackets & Laser
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val stroke = 3.dp.toPx()
                        val cornerLen = 28.dp.toPx()

                        // Top-Left
                        drawLine(NeonCyan, Offset(24f, 24f), Offset(24f + cornerLen, 24f), stroke)
                        drawLine(NeonCyan, Offset(24f, 24f), Offset(24f, 24f + cornerLen), stroke)

                        // Top-Right
                        drawLine(NeonCyan, Offset(w - 24f, 24f), Offset(w - 24f - cornerLen, 24f), stroke)
                        drawLine(NeonCyan, Offset(w - 24f, 24f), Offset(w - 24f, 24f + cornerLen), stroke)

                        // Bottom-Left
                        drawLine(NeonCyan, Offset(24f, h - 24f), Offset(24f + cornerLen, h - 24f), stroke)
                        drawLine(NeonCyan, Offset(24f, h - 24f), Offset(24f, h - 24f - cornerLen), stroke)

                        // Bottom-Right
                        drawLine(NeonCyan, Offset(w - 24f, h - 24f), Offset(w - 24f - cornerLen, h - 24f), stroke)
                        drawLine(NeonCyan, Offset(w - 24f, h - 24f), Offset(w - 24f, h - 24f - cornerLen), stroke)

                        // Sweeping Hologram Laser
                        val laserY = h * laserYRatio
                        drawLine(
                            color = NeonMagenta,
                            start = Offset(24f, laserY),
                            end = Offset(w - 24f, laserY),
                            strokeWidth = 2.5.dp.toPx()
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ALIGN BARCODE IN FRAME",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Auto-detecting UPC / EAN-13 codes",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Error indicator if lookup fails
                if (scanError != null) {
                    Text(
                        text = scanError!!,
                        color = Color(0xFFFF5252),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Direct Barcode Number Input Field (For manual typing or scanner gun input)
                OutlinedTextField(
                    value = manualBarcode,
                    onValueChange = {
                        manualBarcode = it
                        scanError = null
                        val found = FoodDatabase.findByBarcode(it)
                        if (found != null) {
                            onBarcodeScanned(found)
                        }
                    },
                    label = { Text("Barcode Number (e.g. 748927028669)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_barcode_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = NeonSurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = NeonSurfaceCard,
                        unfocusedContainerColor = NeonSurfaceCard
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            val found = FoodDatabase.findByBarcode(manualBarcode)
                            if (found != null) {
                                onBarcodeScanned(found)
                            } else {
                                scanError = "Product not found in database. Try standard staples below!"
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Lookup",
                                tint = NeonCyan
                            )
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // One-Tap Sample Scannable Barcode Chips
                Text(
                    text = "TAP TO SIMULATE SCANNING REAL BARCODE:",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val sampleItems = remember {
                    listOf(
                        FoodDatabase.FOOD_CATALOG[1], // Whey
                        FoodDatabase.FOOD_CATALOG[0], // Chicken
                        FoodDatabase.FOOD_CATALOG[7], // Salmon
                        FoodDatabase.FOOD_CATALOG[4], // Oats
                        FoodDatabase.FOOD_CATALOG[8]  // Avocado
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sampleItems.forEach { sample ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonSurfaceCard)
                                .border(1.dp, NeonSurfaceCardBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    onBarcodeScanned(sample)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(sample.iconEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = sample.name,
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "UPC: ${sample.barcode}",
                                        color = TextTertiary,
                                        fontSize = 9.sp
                                    )
                                }
                            }

                            Text(
                                text = "SCAN ⚡",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
