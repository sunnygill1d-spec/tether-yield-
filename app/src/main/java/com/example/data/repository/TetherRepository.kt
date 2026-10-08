package com.example.data.repository

import com.example.data.local.AdminTreasuryDao
import com.example.data.local.AdminTreasuryEntity
import com.example.data.local.InvestmentDao
import com.example.data.local.InvestmentEntity
import com.example.data.local.NotificationDao
import com.example.data.local.NotificationEntity
import com.example.data.local.ReferralDao
import com.example.data.local.ReferralMemberEntity
import com.example.data.local.TransactionDao
import com.example.data.local.TransactionEntity
import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import com.example.data.model.InvestmentPlansData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class TetherRepository(
    private val userDao: UserDao,
    private val investmentDao: InvestmentDao,
    private val transactionDao: TransactionDao,
    private val referralDao: ReferralDao,
    private val notificationDao: NotificationDao,
    private val adminTreasuryDao: AdminTreasuryDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val adminUser = userDao.getUserByEmail("admin@tetheryield.io")
        if (adminUser == null) {
            // Seed Admin User
            userDao.insertUser(
                UserEntity(
                    id = "admin_master_1",
                    email = "admin@tetheryield.io",
                    fullName = "Master Administrator",
                    passwordHash = "admin123",
                    usdtBalance = 150000.0,
                    stakedBalance = 0.0,
                    totalEarnedYield = 0.0,
                    referralEarnings = 0.0,
                    referralCode = "ADMIN-001",
                    isBiometricEnabled = true,
                    isTwoFactorEnabled = true,
                    role = "ADMIN"
                )
            )

            // Seed Treasury
            adminTreasuryDao.insertOrUpdateTreasury(
                AdminTreasuryEntity(
                    id = 1,
                    totalVaultReserve = 2500000.0,
                    hotWalletBalance = 450000.0,
                    coldWalletBalance = 2050000.0,
                    totalYieldPaidOut = 84520.0,
                    platformFeePool = 14200.0,
                    hotWalletAddress = "TY9xW8q2kPLzM7hQ1jVvR6aB3mN4sK8eD2"
                )
            )
        }

        val demoUser = userDao.getUserByEmail("demo@investor.com")
        if (demoUser == null) {
            val userId = "user_demo_1"
            userDao.insertUser(
                UserEntity(
                    id = userId,
                    email = "demo@investor.com",
                    fullName = "Alex Mercer",
                    passwordHash = "password123",
                    usdtBalance = 250.0,
                    stakedBalance = 300.0,
                    totalEarnedYield = 42.50,
                    referralEarnings = 35.0,
                    referralCode = "TY-84920",
                    isBiometricEnabled = true,
                    isTwoFactorEnabled = true,
                    role = "USER"
                )
            )

            // Seed active investment for demo user: Plan 6 ($100) and Plan 8 ($200)
            investmentDao.insertInvestment(
                InvestmentEntity(
                    id = "inv_1",
                    userId = userId,
                    planId = 6,
                    planName = "Silver Standard",
                    principalAmount = 100.0,
                    dailyRate = 0.005,
                    totalEarned = 14.50,
                    daysElapsed = 29,
                    totalDays = 30,
                    status = "ACTIVE"
                )
            )

            investmentDao.insertInvestment(
                InvestmentEntity(
                    id = "inv_2",
                    userId = userId,
                    planId = 8,
                    planName = "Gold Explorer",
                    principalAmount = 200.0,
                    dailyRate = 0.005,
                    totalEarned = 28.00,
                    daysElapsed = 28,
                    totalDays = 30,
                    status = "ACTIVE"
                )
            )

            // Seed referral downline
            referralDao.insertReferral(
                ReferralMemberEntity(
                    id = "ref_1",
                    referrerId = userId,
                    refereeName = "Sophia Laurent",
                    refereeEmail = "sophia@web3.org",
                    tier = 1,
                    commissionPercent = 5.0,
                    totalContributed = 400.0,
                    commissionEarned = 20.0
                )
            )
            referralDao.insertReferral(
                ReferralMemberEntity(
                    id = "ref_2",
                    referrerId = userId,
                    refereeName = "David Chen",
                    refereeEmail = "chen@crypto.hk",
                    tier = 2,
                    commissionPercent = 3.0,
                    totalContributed = 300.0,
                    commissionEarned = 9.0
                )
            )
            referralDao.insertReferral(
                ReferralMemberEntity(
                    id = "ref_3",
                    referrerId = userId,
                    refereeName = "Elena Rostova",
                    refereeEmail = "elena@vostok.io",
                    tier = 3,
                    commissionPercent = 1.0,
                    totalContributed = 600.0,
                    commissionEarned = 6.0
                )
            )

            // Seed initial transactions
            transactionDao.insertTransaction(
                TransactionEntity(
                    id = "tx_01",
                    userId = userId,
                    type = "DEPOSIT",
                    amount = 500.0,
                    network = "TRC20",
                    toAddress = "TY9xW8q2kPLzM7hQ1jVvR6aB3mN4sK8eD2",
                    txHash = "0x8fa3b7c29d10e541a77e64b8cd9021a8f341",
                    status = "COMPLETED",
                    timestamp = System.currentTimeMillis() - 86400000L * 4
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    id = "tx_02",
                    userId = userId,
                    type = "STAKE",
                    amount = 300.0,
                    network = "INTERNAL",
                    toAddress = "Yield Staking Vault",
                    txHash = "STAKE-LOCK-84920",
                    status = "COMPLETED",
                    timestamp = System.currentTimeMillis() - 86400000L * 3
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    id = "tx_03",
                    userId = userId,
                    type = "DAILY_YIELD",
                    amount = 1.50,
                    network = "INTERNAL",
                    toAddress = "Available Wallet",
                    txHash = "YIELD-DIST-0029",
                    status = "COMPLETED",
                    timestamp = System.currentTimeMillis() - 3600000L * 5
                )
            )

            // Seed notifications
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_1",
                    userId = userId,
                    title = "Daily Yield Accrued (+1.50 USDT)",
                    message = "Your 0.5% daily return for Silver Standard & Gold Explorer plans has been credited.",
                    type = "YIELD"
                )
            )
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_2",
                    userId = userId,
                    title = "Tier 1 Referral Bonus (+20.00 USDT)",
                    message = "Sophia Laurent subscribed to Gold Executive tier. 5% commission credited.",
                    type = "REFERRAL"
                )
            )
        }
    }

    // Auth methods
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)

    fun getUserById(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)

    suspend fun registerUser(
        email: String,
        fullName: String,
        passwordHash: String,
        referralCodeInput: String?
    ): Result<UserEntity> {
        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            return Result.failure(Exception("An account with this email already exists."))
        }
        val newUserId = "user_" + UUID.randomUUID().toString().take(8)
        val generatedRefCode = "TY-" + (10000..99999).random()

        val newUser = UserEntity(
            id = newUserId,
            email = email,
            fullName = fullName,
            passwordHash = passwordHash,
            usdtBalance = 25.0, // Complimentary starter welcome gift
            stakedBalance = 0.0,
            totalEarnedYield = 0.0,
            referralEarnings = 0.0,
            referralCode = generatedRefCode,
            referredBy = referralCodeInput?.trim()?.ifEmpty { null },
            isBiometricEnabled = false,
            isTwoFactorEnabled = false,
            role = if (email.contains("admin")) "ADMIN" else "USER"
        )
        userDao.insertUser(newUser)

        notificationDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = newUserId,
                title = "Welcome to TetherYield! (+25 USDT Gift)",
                message = "Your account is active with 25 USDT trial balance. Explore our 15 daily yield plans.",
                type = "SYSTEM"
            )
        )

        return Result.success(newUser)
    }

    suspend fun resetPassword(email: String): Boolean {
        val user = userDao.getUserByEmail(email) ?: return false
        // Reset password to temporary and notify
        val updated = user.copy(passwordHash = "newpass123")
        userDao.updateUser(updated)
        notificationDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                title = "Security Alert: Password Reset",
                message = "Temporary access key dispatched. Update your credentials in settings.",
                type = "SECURITY"
            )
        )
        return true
    }

    suspend fun updateBiometric(userId: String, enabled: Boolean) {
        userDao.updateBiometric(userId, enabled)
    }

    suspend fun updateTwoFactor(userId: String, enabled: Boolean) {
        userDao.updateTwoFactor(userId, enabled)
    }

    suspend fun updateLanguage(userId: String, lang: String) {
        userDao.updateLanguage(userId, lang)
    }

    // Faucet / Deposit
    suspend fun depositFunds(userId: String, amount: Double, network: String): String {
        userDao.addBalance(userId, amount)
        val txHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(32)
        transactionDao.insertTransaction(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                type = "DEPOSIT",
                amount = amount,
                network = network,
                toAddress = "TY9xW8q2kPLzM7hQ1jVvR6aB3mN4sK8eD2",
                txHash = txHash,
                status = "COMPLETED"
            )
        )
        notificationDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "Deposit Confirmed (+$amount USDT)",
                message = "Received $amount USDT on $network network. Available for instant staking.",
                type = "SYSTEM"
            )
        )
        return txHash
    }

    // Staking Subscription
    suspend fun subscribeToPlan(userId: String, planId: Int): Result<String> {
        val plan = InvestmentPlansData.ALL_PLANS.find { it.id == planId }
            ?: return Result.failure(Exception("Plan not found"))

        val user = userDao.getUserById(userId).firstOrNull()
            ?: return Result.failure(Exception("User not found"))

        if (user.usdtBalance < plan.amountUsdt) {
            return Result.failure(Exception("Insufficient USDT balance. Please deposit or use test faucet."))
        }

        // Deduct from balance and add to staked balance
        userDao.stakeAmount(userId, plan.amountUsdt)

        val invId = "inv_" + UUID.randomUUID().toString().take(8)
        investmentDao.insertInvestment(
            InvestmentEntity(
                id = invId,
                userId = userId,
                planId = plan.id,
                planName = plan.name,
                principalAmount = plan.amountUsdt,
                dailyRate = 0.005,
                totalEarned = 0.0,
                daysElapsed = 0,
                totalDays = plan.durationDays,
                status = "ACTIVE"
            )
        )

        val txHash = "STAKE-" + UUID.randomUUID().toString().take(12).uppercase()
        transactionDao.insertTransaction(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                type = "STAKE",
                amount = plan.amountUsdt,
                network = "INTERNAL",
                toAddress = "0.5% Daily Yield Pool",
                txHash = txHash,
                status = "COMPLETED"
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "Staking Activated: ${plan.name}",
                message = "Locked ${plan.amountUsdt} USDT at 0.5% daily return. Next yield payout in 24 hours.",
                type = "YIELD"
            )
        )

        return Result.success(invId)
    }

    // Daily Yield Harvest / Simulator Tick
    suspend fun harvestDailyYields(userId: String): Double {
        val userInvestments = investmentDao.getAllActiveInvestments().filter { it.userId == userId }
        var totalCredited = 0.0

        for (inv in userInvestments) {
            val dailyYield = inv.principalAmount * inv.dailyRate // 0.5%
            totalCredited += dailyYield

            val updatedInv = inv.copy(
                totalEarned = inv.totalEarned + dailyYield,
                daysElapsed = (inv.daysElapsed + 1).coerceAtMost(inv.totalDays),
                lastPayoutTimestamp = System.currentTimeMillis(),
                status = if (inv.daysElapsed + 1 >= inv.totalDays) "COMPLETED" else "ACTIVE"
            )
            investmentDao.updateInvestment(updatedInv)
        }

        if (totalCredited > 0) {
            userDao.creditYield(userId, totalCredited)
            transactionDao.insertTransaction(
                TransactionEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    type = "DAILY_YIELD",
                    amount = totalCredited,
                    network = "INTERNAL",
                    toAddress = "Available Wallet",
                    txHash = "YIELD-CR-" + System.currentTimeMillis().toString().takeLast(8),
                    status = "COMPLETED"
                )
            )

            notificationDao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    title = "Daily Yield Distributed (+${String.format("%.3f", totalCredited)} USDT)",
                    message = "0.5% daily payout across ${userInvestments.size} active plan(s) added to available balance.",
                    type = "YIELD"
                )
            )
        }
        return totalCredited
    }

    // Automated Withdrawal
    suspend fun requestAutomatedWithdrawal(
        userId: String,
        amount: Double,
        destinationAddress: String,
        network: String
    ): Result<String> {
        val user = userDao.getUserById(userId).firstOrNull()
            ?: return Result.failure(Exception("User not found"))

        if (amount < 5.0) {
            return Result.failure(Exception("Minimum automated withdrawal amount is 5.00 USDT"))
        }

        val networkFee = if (network == "TRC20") 1.0 else if (network == "BEP20") 0.8 else 5.0
        val totalDebit = amount + networkFee

        if (user.usdtBalance < totalDebit) {
            return Result.failure(Exception("Insufficient balance to cover withdrawal amount + $networkFee USDT network fee"))
        }

        if (destinationAddress.length < 24) {
            return Result.failure(Exception("Invalid destination USDT address format"))
        }

        // Deduct from balance
        userDao.addBalance(userId, -totalDebit)

        val txId = UUID.randomUUID().toString()
        val txHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(32)

        // If amount <= 500 USDT, it processes automatically through the smart queue
        val isAutoApproved = amount <= 500.0
        val initialStatus = if (isAutoApproved) "PROCESSING" else "PENDING"

        transactionDao.insertTransaction(
            TransactionEntity(
                id = txId,
                userId = userId,
                type = "WITHDRAWAL",
                amount = amount,
                fee = networkFee,
                network = network,
                toAddress = destinationAddress,
                txHash = txHash,
                status = initialStatus
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "Automated Withdrawal Submitted ($amount USDT)",
                message = if (isAutoApproved)
                    "Dispatched via $network gateway. Blockchain confirmation in progress."
                else
                    "Flagged for high-volume administrator clearance.",
                type = "WITHDRAWAL"
            )
        )

        // Simulate fast automated blockchain confirmation in background for automated queue
        if (isAutoApproved) {
            scope.launch {
                delay(2500)
                transactionDao.updateTransactionStatus(txId, "COMPLETED")
                notificationDao.insertNotification(
                    NotificationEntity(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        title = "Withdrawal Dispatched Successfully",
                        message = "$amount USDT sent to ${destinationAddress.take(6)}...${destinationAddress.takeLast(4)} on $network.",
                        type = "WITHDRAWAL"
                    )
                )
            }
        }

        return Result.success(txHash)
    }

    // Observables
    fun getInvestmentsForUser(userId: String): Flow<List<InvestmentEntity>> =
        investmentDao.getInvestmentsForUser(userId)

    fun getTransactionsForUser(userId: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsForUser(userId)

    fun getReferralsForUser(userId: String): Flow<List<ReferralMemberEntity>> =
        referralDao.getReferralsForUser(userId)

    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(userId)

    fun getAdminTreasury(): Flow<AdminTreasuryEntity?> = adminTreasuryDao.getTreasury()

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getPendingWithdrawals(): Flow<List<TransactionEntity>> = transactionDao.getPendingWithdrawals()

    // Admin Controls
    suspend fun approveWithdrawal(txId: String) {
        transactionDao.updateTransactionStatus(txId, "COMPLETED")
    }

    suspend fun rejectWithdrawal(txId: String, refundUserId: String, amount: Double) {
        transactionDao.updateTransactionStatus(txId, "REJECTED")
        userDao.addBalance(refundUserId, amount)
    }

    suspend fun executeAdminTreasuryTransfer(
        targetAddress: String,
        amount: Double,
        network: String,
        memo: String
    ): Result<String> {
        val treasury = adminTreasuryDao.getTreasury().firstOrNull()
            ?: return Result.failure(Exception("Treasury not initialized"))

        if (treasury.hotWalletBalance < amount) {
            return Result.failure(Exception("Insufficient hot wallet reserve (Available: ${treasury.hotWalletBalance} USDT)"))
        }

        adminTreasuryDao.deductHotWallet(amount)
        val txHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(32)

        transactionDao.insertTransaction(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = "admin_master_1",
                type = "ADMIN_TRANSFER",
                amount = amount,
                network = network,
                toAddress = targetAddress,
                txHash = txHash,
                status = "COMPLETED"
            )
        )

        return Result.success(txHash)
    }
}
