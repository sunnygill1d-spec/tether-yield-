package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val fullName: String,
    val passwordHash: String,
    val usdtBalance: Double,
    val stakedBalance: Double,
    val totalEarnedYield: Double,
    val referralEarnings: Double,
    val referralCode: String,
    val referredBy: String? = null,
    val isBiometricEnabled: Boolean = false,
    val isTwoFactorEnabled: Boolean = false,
    val languageCode: String = "en",
    val role: String = "USER", // "USER" or "ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val planId: Int, // 1 to 15
    val planName: String,
    val principalAmount: Double,
    val dailyRate: Double = 0.005, // 0.5% daily
    val totalEarned: Double = 0.0,
    val daysElapsed: Int = 0,
    val totalDays: Int = 30,
    val lastPayoutTimestamp: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE", // ACTIVE, COMPLETED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String, // DEPOSIT, WITHDRAWAL, STAKE, DAILY_YIELD, REFERRAL_BONUS, ADMIN_TRANSFER
    val amount: Double,
    val fee: Double = 0.0,
    val network: String = "TRC20", // TRC20, ERC20, BEP20
    val toAddress: String = "",
    val txHash: String = "",
    val status: String = "COMPLETED", // COMPLETED, PROCESSING, PENDING, REJECTED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "referrals")
data class ReferralMemberEntity(
    @PrimaryKey val id: String,
    val referrerId: String,
    val refereeName: String,
    val refereeEmail: String,
    val tier: Int, // 1, 2, 3
    val commissionPercent: Double, // 5.0, 3.0, 1.0
    val totalContributed: Double,
    val commissionEarned: Double,
    val joinedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // YIELD, WITHDRAWAL, SECURITY, REFERRAL, SYSTEM
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_treasury")
data class AdminTreasuryEntity(
    @PrimaryKey val id: Int = 1,
    val totalVaultReserve: Double = 2500000.0,
    val hotWalletBalance: Double = 450000.0,
    val coldWalletBalance: Double = 2050000.0,
    val totalYieldPaidOut: Double = 84520.0,
    val platformFeePool: Double = 14200.0,
    val hotWalletAddress: String = "TY9xW8q2kPLzM7hQ1jVvR6aB3mN4sK8eD2"
)
