package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.InvestmentEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserEntity
import com.example.data.model.CryptoTicker
import com.example.ui.components.MiniSparklineChart
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RubyRed
import com.example.ui.theme.TetherTeal
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    user: UserEntity,
    language: AppLanguage,
    onNavigate: (AppScreen) -> Unit
) {
    val investments by viewModel.userInvestments.collectAsState()
    val transactions by viewModel.userTransactions.collectAsState()
    val marketTickers by viewModel.marketTickers.collectAsState()

    val totalBalance = user.usdtBalance + user.stakedBalance + user.totalEarnedYield

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Welcome Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, ${user.fullName.split(" ").firstOrNull() ?: "Investor"} 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "VIP Tier: Ambassador (0.5% Daily Rewards)",
                        fontSize = 12.sp,
                        color = TetherTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Harvest Yield Fast Action Button
                Button(
                    onClick = { viewModel.harvestYield() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TetherTeal,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("harvest_yield_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Harvest",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = Localization.get("action_claim", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Main Vault Portfolio Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(TetherTeal.copy(alpha = 0.6f), CyanAccent.copy(alpha = 0.2f))),
                        RoundedCornerShape(22.dp)
                    ),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    TetherTeal.copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Localization.get("balance_total", language),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TetherTeal.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(TetherTeal)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "+0.5%/day",
                                        color = TetherTeal,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format("%.2f", totalBalance),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "USDT",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TetherTeal,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sub-metrics Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BalanceSubItem(
                                label = Localization.get("balance_available", language),
                                amount = "${String.format("%.2f", user.usdtBalance)} ₮",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            BalanceSubItem(
                                label = Localization.get("balance_staked", language),
                                amount = "${String.format("%.2f", user.stakedBalance)} ₮",
                                color = CyanAccent
                            )
                            BalanceSubItem(
                                label = Localization.get("balance_rewards", language),
                                amount = "+${String.format("%.2f", user.totalEarnedYield)} ₮",
                                color = TetherTeal
                            )
                        }
                    }
                }
            }
        }

        // Quick Action Row (Deposit, Withdraw, Explore Plans, Testnet Faucet)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.ArrowDownward,
                    label = Localization.get("action_deposit", language),
                    color = TetherTeal,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.WALLET) }
                )
                QuickActionButton(
                    icon = Icons.Default.ArrowUpward,
                    label = Localization.get("action_withdraw", language),
                    color = CyanAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.WALLET) }
                )
                QuickActionButton(
                    icon = Icons.Default.TrendingUp,
                    label = Localization.get("nav_plans", language),
                    color = GoldAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppScreen.PLANS) }
                )
                QuickActionButton(
                    icon = Icons.Default.Savings,
                    label = "+Faucet",
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.depositFaucet(100.0, "TRC20") }
                )
            }
        }

        // Live Market Tickers Horizontal Carousel
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Market Live Analytics",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "View All →",
                        fontSize = 12.sp,
                        color = TetherTeal,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigate(AppScreen.MARKETS) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(marketTickers) { ticker ->
                        MarketTickerCard(ticker = ticker)
                    }
                }
            }
        }

        // Active Staking Plans Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Staking (${investments.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "+ Stake More",
                    fontSize = 12.sp,
                    color = TetherTeal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigate(AppScreen.PLANS) }
                )
            }
        }

        if (investments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockClock,
                            contentDescription = null,
                            tint = TetherTeal,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No active investments yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Subscribe to any of the 15 plans starting from 5 USDT to earn 0.5% daily return.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Button(
                            onClick = { onNavigate(AppScreen.PLANS) },
                            colors = ButtonDefaults.buttonColors(containerColor = TetherTeal, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("View 15 Plans")
                        }
                    }
                }
            }
        } else {
            items(investments) { inv ->
                ActiveInvestmentCard(investment = inv)
            }
        }

        // Recent Transactions Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Wallet History →",
                    fontSize = 12.sp,
                    color = TetherTeal,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigate(AppScreen.WALLET) }
                )
            }
        }

        items(transactions.take(5)) { tx ->
            TransactionRowItem(transaction = tx)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BalanceSubItem(label: String, amount: String, color: Color) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun MarketTickerCard(ticker: CryptoTicker) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticker.symbol,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val isPositive = ticker.change24hPercent >= 0
                Text(
                    text = (if (isPositive) "+" else "") + String.format("%.2f%%", ticker.change24hPercent),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) TetherTeal else RubyRed
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (ticker.priceUsd < 10) String.format("$%.4f", ticker.priceUsd) else String.format("$%,.2f", ticker.priceUsd),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            MiniSparklineChart(
                points = ticker.sparkline,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                lineColor = if (ticker.change24hPercent >= 0) TetherTeal else RubyRed
            )
        }
    }
}

@Composable
fun ActiveInvestmentCard(investment: InvestmentEntity) {
    val dailyYield = investment.principalAmount * investment.dailyRate
    val progress = (investment.daysElapsed.toFloat() / investment.totalDays.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(TetherTeal.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "₮",
                            color = TetherTeal,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = investment.planName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Principal: ${investment.principalAmount} USDT",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "+${String.format("%.3f", dailyYield)} USDT/d",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TetherTeal
                    )
                    Text(
                        text = "0.5% Daily",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = TetherTeal,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Day ${investment.daysElapsed} of ${investment.totalDays}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total Earned: +${String.format("%.2f", investment.totalEarned)} USDT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TetherTeal
                )
            }
        }
    }
}

@Composable
fun TransactionRowItem(transaction: TransactionEntity) {
    val isPositive = transaction.type in listOf("DEPOSIT", "DAILY_YIELD", "REFERRAL_BONUS")
    val symbol = if (isPositive) "+" else "-"
    val color = if (isPositive) TetherTeal else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (transaction.type) {
                            "DEPOSIT" -> TetherTeal.copy(alpha = 0.15f)
                            "WITHDRAWAL" -> RubyRed.copy(alpha = 0.15f)
                            "DAILY_YIELD" -> CyanAccent.copy(alpha = 0.15f)
                            "REFERRAL_BONUS" -> GoldAccent.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (transaction.type) {
                        "DEPOSIT" -> "↓"
                        "WITHDRAWAL" -> "↑"
                        "DAILY_YIELD" -> "⚡"
                        "REFERRAL_BONUS" -> "👥"
                        else -> "₮"
                    },
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = when (transaction.type) {
                        "DEPOSIT" -> "USDT Deposit"
                        "WITHDRAWAL" -> "USDT Withdrawal"
                        "DAILY_YIELD" -> "0.5% Daily Yield"
                        "STAKE" -> "Vault Staking Lock"
                        "REFERRAL_BONUS" -> "Affiliate Commission"
                        "ADMIN_TRANSFER" -> "Treasury Transfer"
                        else -> transaction.type
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${transaction.network} • ${transaction.status}",
                    fontSize = 10.sp,
                    color = if (transaction.status == "COMPLETED") TetherTeal else GoldAccent
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$symbol${String.format("%.2f", transaction.amount)} USDT",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = transaction.txHash.take(10) + "...",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
