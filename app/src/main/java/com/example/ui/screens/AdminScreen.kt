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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.local.TransactionEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RubyRed
import com.example.ui.theme.TetherTeal
import com.example.ui.viewmodel.MainViewModel
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    language: AppLanguage,
    onExitAdmin: () -> Unit
) {
    val treasury by viewModel.adminTreasury.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val pendingWithdrawals by viewModel.pendingWithdrawals.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()

    var showAdminTransferDialog by remember { mutableStateOf(false) }
    var targetAddress by remember { mutableStateOf("") }
    var transferAmount by remember { mutableStateOf("") }
    var transferMemo by remember { mutableStateOf("") }
    var transferError by remember { mutableStateOf<String?>(null) }
    var isSubmittingTransfer by remember { mutableStateOf(false) }

    val totalStakedGlobally = allUsers.sumOf { it.stakedBalance }
    val totalVaultReserve = treasury?.totalVaultReserve ?: 2500000.0
    val hotWalletBalance = treasury?.hotWalletBalance ?: 450000.0
    val coldWalletBalance = treasury?.coldWalletBalance ?: 2050000.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.get("admin_title", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "Real-time supervisory console & master treasury control",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onExitAdmin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Return", fontSize = 11.sp)
                }
            }
        }

        // Real-Time Platform Metrics Grid
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = Localization.get("admin_metrics", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = GoldAccent
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AdminMetricTile(
                            label = Localization.get("admin_total_liq", language),
                            value = "${String.format("%,.0f", totalVaultReserve)} ₮",
                            color = TetherTeal
                        )
                        AdminMetricTile(
                            label = Localization.get("admin_active_staked", language),
                            value = "${String.format("%,.0f", totalStakedGlobally)} ₮",
                            color = CyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AdminMetricTile(
                            label = Localization.get("admin_total_users", language),
                            value = "${allUsers.size} Accounts",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        AdminMetricTile(
                            label = "Yield Distributed",
                            value = "${String.format("%,.1f", treasury?.totalYieldPaidOut ?: 84520.0)} ₮",
                            color = GoldAccent
                        )
                    }
                }
            }
        }

        // Administrator Treasury Wallet Control
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = Localization.get("admin_treasury_ctrl", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Host Master USDT Reserve Wallet",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TetherTeal.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Full Custody Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TetherTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Hot Wallet (Operational)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${String.format("%,.2f", hotWalletBalance)} USDT",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TetherTeal
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Cold Vault (Deep Storage)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${String.format("%,.2f", coldWalletBalance)} USDT",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CyanAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Execute Treasury Transfer Button
                    Button(
                        onClick = {
                            targetAddress = ""
                            transferAmount = ""
                            transferMemo = ""
                            transferError = null
                            showAdminTransferDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("admin_transfer_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Localization.get("admin_send_funds", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Pending Withdrawal Clearance Queue
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Localization.get("admin_payout_queue", language) + " (${pendingWithdrawals.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (pendingWithdrawals.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(RubyRed.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Action Required", fontSize = 10.sp, color = RubyRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (pendingWithdrawals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No pending withdrawals in the administrator clearance queue. All small automated withdrawals were processed instantly.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(pendingWithdrawals) { tx ->
                PendingWithdrawalItem(
                    transaction = tx,
                    language = language,
                    onApprove = { viewModel.approveWithdrawal(tx.id) },
                    onReject = { viewModel.rejectWithdrawal(tx.id, tx.userId, tx.amount + tx.fee) }
                )
            }
        }

        // User Accounts Overview
        item {
            Text(
                text = "Registered User Accounts (${allUsers.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(allUsers) { userItem ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userItem.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            if (userItem.role == "ADMIN") {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GoldAccent.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("HOST", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                }
                            }
                        }
                        Text(
                            text = "${userItem.email} • Code: ${userItem.referralCode}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${String.format("%.2f", userItem.usdtBalance)} ₮",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TetherTeal
                        )
                        Text(
                            text = "Staked: ${userItem.stakedBalance.toInt()} ₮",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Admin Treasury Fund Transfer Modal
    if (showAdminTransferDialog) {
        AlertDialog(
            onDismissRequest = { if (!isSubmittingTransfer) showAdminTransferDialog = false },
            title = {
                Text("Execute Host Treasury Transfer", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Transfer USDT from the Platform Hot Wallet directly to an external cold storage or partner address.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = targetAddress,
                        onValueChange = { targetAddress = it },
                        label = { Text("Destination Address (TRC20/ERC20)") },
                        placeholder = { Text("e.g. TY9xW8q...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_target_address_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = transferAmount,
                        onValueChange = { transferAmount = it },
                        label = { Text("Transfer Amount (USDT)") },
                        placeholder = { Text("Available: ${hotWalletBalance.toInt()} USDT") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_transfer_amount_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = transferMemo,
                        onValueChange = { transferMemo = it },
                        label = { Text("Transfer Memo / Purpose") },
                        placeholder = { Text("e.g. Liquidity rebalancing") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    if (transferError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = transferError!!,
                            color = RubyRed,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = transferAmount.toDoubleOrNull()
                        if (amt == null || amt <= 0.0) {
                            transferError = "Please enter a valid transfer amount"
                            return@Button
                        }
                        if (targetAddress.length < 20) {
                            transferError = "Please enter a valid destination address"
                            return@Button
                        }

                        isSubmittingTransfer = true
                        transferError = null
                        viewModel.adminTreasuryTransfer(
                            targetAddress = targetAddress,
                            amount = amt,
                            network = "TRC20",
                            memo = transferMemo
                        ) { success, msg ->
                            isSubmittingTransfer = false
                            if (success) {
                                showAdminTransferDialog = false
                            } else {
                                transferError = msg
                            }
                        }
                    },
                    modifier = Modifier.testTag("submit_admin_transfer_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                    enabled = !isSubmittingTransfer
                ) {
                    if (isSubmittingTransfer) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Broadcast Transfer")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAdminTransferDialog = false },
                    enabled = !isSubmittingTransfer
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminMetricTile(label: String, value: String, color: Color) {
    Column {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
    }
}

@Composable
fun PendingWithdrawalItem(
    transaction: TransactionEntity,
    language: AppLanguage,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "User: ${transaction.userId}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "To: ${transaction.toAddress.take(12)}... (${transaction.network})",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${String.format("%.2f", transaction.amount)} USDT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = TetherTeal
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TetherTeal, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Localization.get("admin_approve", language), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed.copy(alpha = 0.2f), contentColor = RubyRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Localization.get("admin_reject", language), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
