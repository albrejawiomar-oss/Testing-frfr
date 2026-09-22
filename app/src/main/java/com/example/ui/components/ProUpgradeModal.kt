package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun ProUpgradeModal(
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onConfirmUpgrade: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("pro_upgrade_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NeonSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(NeonAmber, NeonMagenta, NeonCyan)
                ),
                width = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Modal Header Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(NeonAmber.copy(alpha = 0.35f), Color.Transparent)
                            )
                        )
                        .border(1.5.dp, NeonAmber, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Pro Crown",
                        tint = NeonAmber,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "NEONFIT PRO UNLOCK",
                    color = NeonAmber,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "One-Time $12 Lifetime Access Pass",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Feature Highlights
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeonSurfaceCard)
                        .border(1.dp, NeonSurfaceCardBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProFeatureRow(
                        icon = Icons.Default.Science,
                        title = "Full-Spectrum Deficiency Protocols",
                        desc = "All clinical nutrient remedies, co-factor synergies & pro meal plans"
                    )
                    ProFeatureRow(
                        icon = Icons.Default.Sync,
                        title = "Automated Diet Tracker Sync",
                        desc = "Directly map macro targets and deficiency habits to your daily tracker"
                    )
                    ProFeatureRow(
                        icon = Icons.Default.Palette,
                        title = "Custom Cyber Skins & Wallpapers",
                        desc = "Unlock Quantum Nexus & VIP chromatic HUD aesthetics"
                    )
                    ProFeatureRow(
                        icon = Icons.Default.Insights,
                        title = "Advanced AI Biometric Analytics",
                        desc = "Deep hypertrophy split reasoning & macro projections"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dedicated Humble Charity Pledge Trust Badge
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("charity_pledge_badge"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0D2418)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(NeonGreen.copy(alpha = 0.8f), Color(0xFF00FF88))
                        ),
                        width = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🌿",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "5% of every sale goes directly to global humanitarian relief efforts.",
                            color = Color(0xFFD1FAE5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Upgrade / Action Buttons
                Button(
                    onClick = onConfirmUpgrade,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_pro_upgrade_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonAmber
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (userProfile.isProUnlocked) "PRO ACTIVE • THANK YOU" else "UNLOCK PRO ($12 ONE-TIME)",
                        color = NeonBackground,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("dismiss_pro_modal_btn")
                ) {
                    Text(
                        text = "Continue on Free Tier",
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }
    }
}
