package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SHOP_ITEMS
import com.example.model.ShopItem
import com.example.model.ShopItemType
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun StoreScreen(
    userProfile: UserProfile,
    onPurchaseItem: (ShopItem) -> Boolean,
    onEquipItem: (ShopItem) -> Unit,
    onOpenProModal: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf(ShopItemType.SKIN) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val filteredItems = remember(selectedFilter) {
        SHOP_ITEMS.filter { it.type == selectedFilter }
    }

    Scaffold(
        containerColor = NeonBackground,
        snackbarHost = {
            snackbarMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = NeonSurface,
                    contentColor = TextPrimary,
                    action = {
                        TextButton(onClick = { snackbarMessage = null }) {
                            Text("OK", color = NeonCyan)
                        }
                    }
                ) {
                    Text(msg)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
        ) {
            // Header: XP Balance and Pro Status
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shop_balance_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NeonSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(NeonAmber.copy(alpha = 0.6f), NeonMagenta.copy(alpha = 0.4f))
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
                            Column {
                                Text(
                                    text = "CYBERNETIC ARSENAL",
                                    color = NeonAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.5.sp
                                )
                                Text(
                                    text = "Skins & Wallpapers",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // XP Balance Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NeonAmber.copy(alpha = 0.15f))
                                    .border(1.dp, NeonAmber, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "⚡ ${userProfile.xp} XP",
                                    color = NeonAmber,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Freemium Pro Gate Banner
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenProModal() }
                                .testTag("pro_store_banner"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (userProfile.isProUnlocked) Color(0xFF0D2418) else NeonSurfaceCard
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(
                                    listOf(NeonAmber, NeonGreen)
                                ),
                                width = 1.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "Pro",
                                        tint = NeonAmber,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (userProfile.isProUnlocked) "PRO ACTIVE • LIFETIME ACCESS" else "UPGRADE TO PRO ($12 ONE-TIME)",
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (userProfile.isProUnlocked) "All exclusive cosmetics & analytics unlocked" else "Unlocks custom themes, cloud backup & charity pledge",
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Text(
                                    text = if (userProfile.isProUnlocked) "ACTIVE" else "VIEW →",
                                    color = NeonAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            // Category Tab Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == ShopItemType.SKIN,
                        onClick = { selectedFilter = ShopItemType.SKIN },
                        label = { Text("App HUD Skins") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = NeonBackground,
                            containerColor = NeonSurfaceCard,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = selectedFilter == ShopItemType.WALLPAPER,
                        onClick = { selectedFilter = ShopItemType.WALLPAPER },
                        label = { Text("Matrix Wallpapers") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonMagenta,
                            selectedLabelColor = NeonBackground,
                            containerColor = NeonSurfaceCard,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Items List
            items(filteredItems, key = { it.id }) { item ->
                val isPurchased = userProfile.purchasedItemIds.contains(item.id)
                val isActive = when (item.type) {
                    ShopItemType.SKIN -> userProfile.activeSkinId == item.id
                    ShopItemType.WALLPAPER -> userProfile.activeWallpaperId == item.id
                }
                val canAfford = userProfile.xp >= item.costXp
                val isProLocked = item.isProExclusive && !userProfile.isProUnlocked

                ShopItemCard(
                    item = item,
                    isPurchased = isPurchased,
                    isActive = isActive,
                    canAfford = canAfford,
                    isProLocked = isProLocked,
                    onAction = {
                        if (isProLocked) {
                            onOpenProModal()
                        } else if (isPurchased) {
                            onEquipItem(item)
                            snackbarMessage = "Equipped ${item.title}"
                        } else {
                            val success = onPurchaseItem(item)
                            if (success) {
                                snackbarMessage = "Purchased & equipped ${item.title}!"
                            } else {
                                snackbarMessage = "Insufficient XP! Log workouts to earn more."
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ShopItemCard(
    item: ShopItem,
    isPurchased: Boolean,
    isActive: Boolean,
    canAfford: Boolean,
    isProLocked: Boolean,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shop_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeonSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                if (isActive) {
                    listOf(NeonGreen, NeonCyan)
                } else if (isProLocked) {
                    listOf(NeonAmber.copy(alpha = 0.5f), Color.Transparent)
                } else {
                    listOf(Color(item.previewHex).copy(alpha = 0.5f), Color(item.accentHex).copy(alpha = 0.5f))
                }
            ),
            width = if (isActive) 1.8.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Swatch Preview
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(item.previewHex), Color(item.accentHex))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.iconEmoji,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.isProExclusive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonAmber.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PRO",
                                color = NeonAmber,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                Text(
                    text = item.subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (!isPurchased && !isProLocked) {
                    Text(
                        text = if (item.costXp == 0) "Free Default" else "${item.costXp} XP",
                        color = NeonAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Button
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isActive -> NeonGreen
                        isProLocked -> NeonAmber
                        isPurchased -> NeonSurfaceCard
                        canAfford -> NeonCyan
                        else -> TextTertiary
                    }
                ),
                modifier = Modifier.height(38.dp)
            ) {
                Text(
                    text = when {
                        isActive -> "EQUIPPED"
                        isProLocked -> "UNLOCK"
                        isPurchased -> "EQUIP"
                        canAfford -> "BUY"
                        else -> "LOCK"
                    },
                    color = if (isActive || canAfford) NeonBackground else TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
