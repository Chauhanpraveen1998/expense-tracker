package com.praveen.expensetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.praveen.expensetracker.data.local.dao.AccountDao
import com.praveen.expensetracker.data.local.dao.TransactionDao
import com.praveen.expensetracker.data.local.entity.AccountEntity
import com.praveen.expensetracker.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        AccountEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    
    companion object {
        const val DATABASE_NAME = "expense_tracker_db"
    }
}
