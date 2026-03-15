package com.praveen.expensetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.praveen.expensetracker.data.local.entity.TransactionEntity
import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TransactionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)
    
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
    
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
    
    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: String)
    
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
    
    @Query("SELECT * FROM transactions ORDER BY date_time DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?
    
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    fun getTransactionByIdFlow(transactionId: String): Flow<TransactionEntity?>
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date_time DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date_time DESC")
    fun getTransactionsByCategory(category: Category): Flow<List<TransactionEntity>>
    
    @Query("""
        SELECT * FROM transactions 
        WHERE date_time >= :startDate AND date_time <= :endDate 
        ORDER BY date_time DESC
    """)
    fun getTransactionsByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<TransactionEntity>>
    
    @Query("""
        SELECT * FROM transactions 
        WHERE merchant_name LIKE '%' || :query || '%' 
        OR note LIKE '%' || :query || '%'
        ORDER BY date_time DESC
    """)
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    suspend fun getTotalByType(type: TransactionType): Double?
    
    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE type = :type 
        AND date_time >= :startDate AND date_time <= :endDate
    """)
    suspend fun getTotalByTypeAndDateRange(
        type: TransactionType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Double?
    
    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE category = :category 
        AND date_time >= :startDate AND date_time <= :endDate
    """)
    suspend fun getTotalByCategory(
        category: Category,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Double?
    
    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int
    
    @Query("""
        SELECT * FROM transactions 
        ORDER BY date_time DESC 
        LIMIT :limit
    """)
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM transactions WHERE sms_hash = :smsHash)")
    suspend fun existsBySmsHash(smsHash: String): Boolean
    
    @Query("""
        SELECT category, SUM(amount) as total 
        FROM transactions 
        WHERE type = 'EXPENSE' 
        AND date_time >= :startDate AND date_time <= :endDate
        GROUP BY category 
        ORDER BY total DESC
    """)
    suspend fun getCategorySpendingSummary(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<CategorySpendingTuple>
    
    @Query("""
        SELECT DATE(date_time) as date, SUM(amount) as total 
        FROM transactions 
        WHERE type = 'EXPENSE' 
        AND date_time >= :startDate AND date_time <= :endDate
        GROUP BY DATE(date_time) 
        ORDER BY date DESC
    """)
    suspend fun getDailySpending(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<DailySpendingTuple>
}

data class CategorySpendingTuple(
    val category: Category,
    val total: Double
)

data class DailySpendingTuple(
    val date: String,
    val total: Double
)
