package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        InvestmentEntity::class,
        TransactionEntity::class,
        ReferralMemberEntity::class,
        NotificationEntity::class,
        AdminTreasuryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun investmentDao(): InvestmentDao
    abstract fun transactionDao(): TransactionDao
    abstract fun referralDao(): ReferralDao
    abstract fun notificationDao(): NotificationDao
    abstract fun adminTreasuryDao(): AdminTreasuryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tether_yield_vault.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
