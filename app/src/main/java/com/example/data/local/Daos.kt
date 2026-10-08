package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET usdtBalance = :balance WHERE id = :userId")
    suspend fun updateBalance(userId: String, balance: Double)

    @Query("UPDATE users SET usdtBalance = usdtBalance + :amount WHERE id = :userId")
    suspend fun addBalance(userId: String, amount: Double)

    @Query("UPDATE users SET usdtBalance = usdtBalance - :amount, stakedBalance = stakedBalance + :amount WHERE id = :userId")
    suspend fun stakeAmount(userId: String, amount: Double)

    @Query("UPDATE users SET usdtBalance = usdtBalance + :earned, totalEarnedYield = totalEarnedYield + :earned WHERE id = :userId")
    suspend fun creditYield(userId: String, earned: Double)

    @Query("UPDATE users SET isBiometricEnabled = :enabled WHERE id = :userId")
    suspend fun updateBiometric(userId: String, enabled: Boolean)

    @Query("UPDATE users SET isTwoFactorEnabled = :enabled WHERE id = :userId")
    suspend fun updateTwoFactor(userId: String, enabled: Boolean)

    @Query("UPDATE users SET languageCode = :lang WHERE id = :userId")
    suspend fun updateLanguage(userId: String, lang: String)
}

@Dao
interface InvestmentDao {
    @Query("SELECT * FROM investments WHERE userId = :userId ORDER BY createdAt DESC")
    fun getInvestmentsForUser(userId: String): Flow<List<InvestmentEntity>>

    @Query("SELECT * FROM investments WHERE status = 'ACTIVE'")
    suspend fun getAllActiveInvestments(): List<InvestmentEntity>

    @Query("SELECT * FROM investments ORDER BY createdAt DESC")
    fun getAllInvestments(): Flow<List<InvestmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: InvestmentEntity)

    @Update
    suspend fun updateInvestment(investment: InvestmentEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE status = 'PENDING' AND type = 'WITHDRAWAL' ORDER BY timestamp ASC")
    fun getPendingWithdrawals(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("UPDATE transactions SET status = :status WHERE id = :txId")
    suspend fun updateTransactionStatus(txId: String, status: String)
}

@Dao
interface ReferralDao {
    @Query("SELECT * FROM referrals WHERE referrerId = :referrerId ORDER BY joinedTimestamp DESC")
    fun getReferralsForUser(referrerId: String): Flow<List<ReferralMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(referral: ReferralMemberEntity)

    @Query("SELECT SUM(commissionEarned) FROM referrals WHERE referrerId = :referrerId")
    fun getTotalCommission(referrerId: String): Flow<Double?>
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)
}

@Dao
interface AdminTreasuryDao {
    @Query("SELECT * FROM admin_treasury WHERE id = 1 LIMIT 1")
    fun getTreasury(): Flow<AdminTreasuryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTreasury(treasury: AdminTreasuryEntity)

    @Query("UPDATE admin_treasury SET hotWalletBalance = hotWalletBalance - :amount, totalVaultReserve = totalVaultReserve - :amount WHERE id = 1")
    suspend fun deductHotWallet(amount: Double)

    @Query("UPDATE admin_treasury SET hotWalletBalance = hotWalletBalance + :amount, totalVaultReserve = totalVaultReserve + :amount WHERE id = 1")
    suspend fun creditHotWallet(amount: Double)
}
