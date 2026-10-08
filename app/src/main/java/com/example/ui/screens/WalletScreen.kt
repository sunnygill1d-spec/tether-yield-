package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.ui.components.SimulatedQrCodeView
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.RubyRed
import com.example.ui.theme.TetherTeal
import com.example.ui.viewmodel.MainViewModel
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun WalletScreen(
    viewModel: MainViewModel,
    user: UserEntity,
    language: AppLanguage
) {
    val context = LocalContext.current
    val transactions by viewModel.userTransactions.collectAsState()

    var activeNetworkTab by remember { mutableIntStateOf(0) } // 0: TRC-20, 1: BEP-20, 2: ERC-20
    val networkNames = listOf("TRC-20", "BEP-20", "ERC-20")
    val depositAddresses = listOf(
        "TY9xW8q2kPLzM7hQ1jVvR6aB3mN4sK8eD2",
        "0x71C813237a34e06d912B9D4353E5C775276D36a8",
        "0xdAC17F958D2ee523a2206206994597C13D831ec7"
    )

    var showWithdrawModal by remember { mutableStateOf(false) }
    var withdrawAmountText by remember { mutableStateOf("") }
    var withdrawAddressText by remember { mutableStateOf("TY" + "84920".repeat(5).take(32)) }
    var withdrawError by remember { mutableStateOf<String?>(null) }
    var isProcessingWithdraw by remember { mutableStateOf(false) }

    // Transaction filter tab (0: All, 1: Deposits, 2: Withdrawals, 3: Yields)
    var txFilterTab by remember { mutableIntStateOf(0) }

    val filteredTransactions = transactions.filter { tx ->
        when (txFilterTab) {
            1 -> tx.type == "DEPOSIT"
            2 -> tx.type == "WITHDRAWAL"
            3 -> tx.type in listOf("DAILY_YIELD", "STAKE", "REFERRAL_BONUS")
            else -> true
        }
    }

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("USDT Address", text)
        clipboard.setPrimaryClip(clip)
        viewModel.showMessage(Localization.get("action_copied", language))
    }

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
                text = Localization.get("wallet_title", language),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Multi-chain asset management & automated withdrawal gateway",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Wallet Balance Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = Localization.get("balance_available", language),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.2f", user.usdtBalance),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "USDT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TetherTeal,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.depositFaucet(100.0, networkNames[activeNetworkTab]) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("faucet_deposit_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TetherTeal,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Localization.get("faucet_btn", language), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                withdrawAmountText = ""
                                withdrawError = null
                                showWithdrawModal = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("withdraw_modal_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Localization.get("action_withdraw", language), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Deposit Section with Multi-Network Tabs & QR Code
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Deposit USDT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = TetherTeal, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Smart Contract Verified", fontSize = 11.sp, color = TetherTeal)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Network Tabs
                    TabRow(
                        selectedTabIndex = activeNetworkTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = TetherTeal,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.tabIndicatorOffset(tabPositions[activeNetworkTab]),
                                color = TetherTeal,
                                height = 3.dp
                            )
                        }
                    ) {
                        networkNames.forEachIndexed { index, name ->
                            Tab(
                                selected = activeNetworkTab == index,
                                onClick = { activeNetworkTab = index },
                                text = {
                                    Text(
                                        name,
                                        fontWeight = if (activeNetworkTab == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Code
                    val address = depositAddresses[activeNetworkTab]
                    SimulatedQrCodeView(textData = address)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Address Box with Copy Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .clickable { copyToClipboard(address) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Your ${networkNames[activeNetworkTab]} Deposit Address:",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = address,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { copyToClipboard(address) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TetherTeal, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Comprehensive Transaction History Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaction History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${filteredTransactions.size} Records",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Transaction Filter Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Deposits", "Withdrawals", "Yields").forEachIndexed { index, label ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (txFilterTab == index) TetherTeal else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { txFilterTab = index }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (txFilterTab == index) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions found in this category.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(filteredTransactions) { tx ->
                TransactionRowItem(transaction = tx)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Automated Withdrawal Dialog
    if (showWithdrawModal) {
        AlertDialog(
            onDismissRequest = { if (!isProcessingWithdraw) showWithdrawModal = false },
            title = {
                Text(Localization.get("withdraw_auto_title", language), fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Automated instant blockchain withdrawal. Processed by smart contracts within 60 seconds.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Destination Address
                    OutlinedTextField(
                        value = withdrawAddressText,
                        onValueChange = { withdrawAddressText = it },
                        label = { Text(Localization.get("withdraw_address", language)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_address_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TetherTeal)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Amount Field
                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        label = { Text(Localization.get("withdraw_amount", language)) },
                        placeholder = { Text("e.g. 50.0") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_amount_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TetherTeal)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Network Gas Fee: 1.00 USDT",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Available: ${String.format("%.2f", user.usdtBalance)} ₮",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TetherTeal
                        )
                    }

                    if (withdrawError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = withdrawError!!,
                            color = RubyRed,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = withdrawAmountText.toDoubleOrNull()
                        if (amount == null || amount < 5.0) {
                            withdrawError = "Minimum withdrawal is 5.00 USDT"
                            return@Button
                        }
                        if (withdrawAddressText.length < 24) {
                            withdrawError = "Please enter a valid destination address"
                            return@Button
                        }

                        isProcessingWithdraw = true
                        withdrawError = null
                        viewModel.requestWithdrawal(
                            amount = amount,
                            address = withdrawAddressText,
                            network = "TRC20"
                        ) { success, msg ->
                            isProcessingWithdraw = false
                            if (success) {
                                showWithdrawModal = false
                            } else {
                                withdrawError = msg
                            }
                        }
                    },
                    modifier = Modifier.testTag("submit_withdraw_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TetherTeal,
                        contentColor = Color.Black
                    ),
                    enabled = !isProcessingWithdraw
                ) {
                    if (isProcessingWithdraw) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Process Automated Withdrawal")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showWithdrawModal = false },
                    enabled = !isProcessingWithdraw
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
