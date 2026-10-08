package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TetherTeal
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.UiMessage
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun AppHeader(
    currentScreen: AppScreen,
    currentLanguage: AppLanguage,
    unreadNotificationsCount: Int,
    isAdminUser: Boolean,
    onLanguageSelected: (AppLanguage) -> Unit,
    onNotificationClick: () -> Unit,
    onAdminToggleClick: () -> Unit
) {
    var showLanguageMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(TetherTeal, Color(0xFF06B6D4))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "₮",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = Localization.get("app_title", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TetherTeal.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "USDT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TetherTeal
                            )
                        }
                    }
                    Text(
                        text = "0.5% Daily Yield",
                        fontSize = 11.sp,
                        color = TetherTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Action Icons (Language, Notifications, Admin Toggle)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language Switcher Dropdown
                Box {
                    IconButton(
                        onClick = { showLanguageMenu = true },
                        modifier = Modifier.testTag("language_button")
                    ) {
                        Text(text = currentLanguage.flagEmoji, fontSize = 20.sp)
                    }

                    DropdownMenu(
                        expanded = showLanguageMenu,
                        onDismissRequest = { showLanguageMenu = false }
                    ) {
                        AppLanguage.values().forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(lang.flagEmoji, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            lang.displayName,
                                            fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    onLanguageSelected(lang)
                                    showLanguageMenu = false
                                }
                            )
                        }
                    }
                }

                // Notification Bell
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.testTag("notification_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationsCount > 0) {
                                Badge(
                                    containerColor = TetherTeal,
                                    contentColor = Color.Black
                                ) {
                                    Text(text = unreadNotificationsCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Admin Console Access Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (currentScreen == AppScreen.ADMIN)
                                Color(0xFFF59E0B).copy(alpha = 0.25f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onAdminToggleClick() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("admin_toggle_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Console",
                            tint = if (currentScreen == AppScreen.ADMIN) Color(0xFFF59E0B) else TetherTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentScreen == AppScreen.ADMIN) "Exit Host" else "Host Admin",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentScreen == AppScreen.ADMIN) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigation(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    language: AppLanguage
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.DASHBOARD,
            onClick = { onNavigate(AppScreen.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text(Localization.get("nav_dashboard", language), fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = TetherTeal.copy(alpha = 0.2f),
                selectedIconColor = TetherTeal,
                selectedTextColor = TetherTeal
            ),
            modifier = Modifier.testTag("nav_dashboard")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.PLANS,
            onClick = { onNavigate(AppScreen.PLANS) },
            icon = { Icon(Icons.Default.TrendingUp, contentDescription = "Plans") },
            label = { Text(Localization.get("nav_plans", language), fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = TetherTeal.copy(alpha = 0.2f),
                selectedIconColor = TetherTeal,
                selectedTextColor = TetherTeal
            ),
            modifier = Modifier.testTag("nav_plans")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.WALLET,
            onClick = { onNavigate(AppScreen.WALLET) },
            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
            label = { Text(Localization.get("nav_wallet", language), fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = TetherTeal.copy(alpha = 0.2f),
                selectedIconColor = TetherTeal,
                selectedTextColor = TetherTeal
            ),
            modifier = Modifier.testTag("nav_wallet")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.MARKETS,
            onClick = { onNavigate(AppScreen.MARKETS) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Markets") },
            label = { Text(Localization.get("nav_analytics", language), fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = TetherTeal.copy(alpha = 0.2f),
                selectedIconColor = TetherTeal,
                selectedTextColor = TetherTeal
            ),
            modifier = Modifier.testTag("nav_markets")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.AFFILIATE,
            onClick = { onNavigate(AppScreen.AFFILIATE) },
            icon = { Icon(Icons.Default.People, contentDescription = "Affiliate") },
            label = { Text(Localization.get("nav_referral", language), fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = TetherTeal.copy(alpha = 0.2f),
                selectedIconColor = TetherTeal,
                selectedTextColor = TetherTeal
            ),
            modifier = Modifier.testTag("nav_affiliate")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.SETTINGS,
            onClick = { onNavigate(AppScreen.SETTINGS) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text(Localization.get("nav_settings", language), fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = TetherTeal.copy(alpha = 0.2f),
                selectedIconColor = TetherTeal,
                selectedTextColor = TetherTeal
            ),
            modifier = Modifier.testTag("nav_settings")
        )
    }
}

@Composable
fun PushNotificationSnackbar(
    message: UiMessage?,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        if (message != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (message.isError) Color(0xFFEF4444) else Color(0xFF0F2F24),
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (message.isError) Color(0xFFFCA5A5) else TetherTeal.copy(alpha = 0.6f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (message.isError) "⚠️" else "🔔",
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = message.text,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniSparklineChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = TetherTeal
) {
    if (points.isEmpty()) return

    Canvas(modifier = modifier) {
        val min = points.minOrNull() ?: 0f
        val max = points.maxOrNull() ?: 1f
        val range = if (max - min == 0f) 1f else max - min

        val stepX = size.width / (points.size - 1).coerceAtLeast(1)
        val path = Path()

        points.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedY = (value - min) / range
            val y = size.height - (normalizedY * size.height)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun SimulatedQrCodeView(
    textData: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val blockSize = size.width / 15f
            // Draw authentic looking QR code matrix pattern
            val hash = textData.hashCode()
            for (row in 0 until 15) {
                for (col in 0 until 15) {
                    // Position detection corners
                    val isCorner1 = row < 4 && col < 4
                    val isCorner2 = row < 4 && col > 10
                    val isCorner3 = row > 10 && col < 4

                    val shouldFill = if (isCorner1 || isCorner2 || isCorner3) {
                        (row == 0 || row == 3 || col == 0 || col == 3) || (row == 1 && col == 1)
                    } else {
                        ((hash xor (row * 31 + col * 17)) and (1 shl (row + col) % 32)) != 0
                    }

                    if (shouldFill) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(col * blockSize, row * blockSize),
                            size = androidx.compose.ui.geometry.Size(blockSize * 0.95f, blockSize * 0.95f)
                        )
                    }
                }
            }
        }
    }
}
