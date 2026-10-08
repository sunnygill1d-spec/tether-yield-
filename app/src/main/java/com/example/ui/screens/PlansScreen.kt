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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvestmentPlan
import com.example.data.model.InvestmentPlansData
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TetherTeal
import com.example.ui.viewmodel.MainViewModel
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun PlansScreen(
    viewModel: MainViewModel,
    language: AppLanguage
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedPlanToStake by remember { mutableStateOf<InvestmentPlan?>(null) }
    var showStakeConfirmDialog by remember { mutableStateOf(false) }

    // Profit Projection Calculator States
    var calcAmount by remember { mutableFloatStateOf(100f) }
    var calcDays by remember { mutableIntStateOf(30) }
    var isCompoundDaily by remember { mutableStateOf(false) }

    val dailyRate = 0.005 // 0.5% daily
    val projectedYield = if (isCompoundDaily) {
        val compounded = calcAmount * Math.pow(1.0 + dailyRate, calcDays.toDouble())
        compounded - calcAmount
    } else {
        calcAmount * dailyRate * calcDays
    }
    val totalProjectedReturn = calcAmount + projectedYield

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = Localization.get("plans_title", language),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = Localization.get("plans_subtitle", language),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Interactive Profit Projection Calculator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TetherTeal.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = TetherTeal,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("calculator_title", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Compounding Toggle Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCompoundDaily) TetherTeal.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { isCompoundDaily = !isCompoundDaily }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isCompoundDaily) "⚡ Compounded" else "Simple Yield",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCompoundDaily) TetherTeal else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Investment Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = Localization.get("calc_amount", language),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${calcAmount.toInt()} USDT",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TetherTeal
                        )
                    }

                    Slider(
                        value = calcAmount,
                        onValueChange = { calcAmount = it },
                        valueRange = 5f..1000f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = TetherTeal,
                            activeTrackColor = TetherTeal
                        ),
                        modifier = Modifier.testTag("calculator_amount_slider")
                    )

                    // Duration selector pills
                    Text(
                        text = Localization.get("calc_duration", language),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(7, 15, 30, 60, 90).forEach { days ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (calcDays == days) TetherTeal else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { calcDays = days }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${days}d",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (calcDays == days) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Projection Results Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Payout: +${String.format("%.3f", calcAmount * dailyRate)} USDT",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Net Projected Profit: +${String.format("%.2f", projectedYield)} USDT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TetherTeal
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total Maturity:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${String.format("%.2f", totalProjectedReturn)} ₮",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccent
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Available Staking Vaults (15 Tiers)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // List of all 15 plans
        items(InvestmentPlansData.ALL_PLANS) { plan ->
            PlanItemCard(
                plan = plan,
                language = language,
                userBalance = currentUser?.usdtBalance ?: 0.0,
                onStakeClick = {
                    selectedPlanToStake = plan
                    showStakeConfirmDialog = true
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Staking Confirmation Dialog
    if (showStakeConfirmDialog && selectedPlanToStake != null) {
        val plan = selectedPlanToStake!!
        val balance = currentUser?.usdtBalance ?: 0.0
        val hasEnough = balance >= plan.amountUsdt

        AlertDialog(
            onDismissRequest = { showStakeConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = TetherTeal
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stake into ${plan.name}")
                }
            },
            text = {
                Column {
                    Text(
                        text = "You are locking ${plan.amountUsdt} USDT into this 30-day smart liquidity vault.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• 0.5% Daily Return: +${String.format("%.3f", plan.dailyYieldUsdt)} USDT / day",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = TetherTeal
                    )
                    Text(
                        text = "• 30-Day Total Yield: +${String.format("%.2f", plan.monthlyYieldUsdt)} USDT",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = CyanAccent
                    )
                    Text(
                        text = "• Your Available Balance: ${String.format("%.2f", balance)} USDT",
                        fontSize = 12.sp,
                        color = if (hasEnough) MaterialTheme.colorScheme.onSurface else Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (!hasEnough) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Insufficient balance! Please use the Testnet Faucet or deposit USDT first.",
                            fontSize = 11.sp,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.subscribePlan(plan.id)
                        showStakeConfirmDialog = false
                    },
                    enabled = hasEnough,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TetherTeal,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("confirm_stake_button")
                ) {
                    Text("Confirm Stake")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStakeConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PlanItemCard(
    plan: InvestmentPlan,
    language: AppLanguage,
    userBalance: Double,
    onStakeClick: () -> Unit
) {
    val canAfford = userBalance >= plan.amountUsdt

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (plan.popular) TetherTeal.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(16.dp)
            ),
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(plan.colorHex).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${plan.id}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(plan.colorHex)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = plan.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (plan.popular) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GoldAccent)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "POPULAR",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${plan.tierCategory} Tier • 30 Days Lock",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${plan.amountUsdt.toInt()} USDT",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = TetherTeal
                    )
                    Text(
                        text = "Principal",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = plan.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Yield metrics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Daily Return (0.5%)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "+${String.format("%.3f", plan.dailyYieldUsdt)} USDT/day",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TetherTeal
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Monthly Yield (15.0%)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "+${String.format("%.2f", plan.monthlyYieldUsdt)} USDT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            Button(
                onClick = onStakeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("stake_plan_${plan.id}_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) TetherTeal else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (canAfford) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (canAfford) "Stake ${plan.amountUsdt.toInt()} USDT" else "Insufficient USDT (${plan.amountUsdt.toInt()} needed)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
