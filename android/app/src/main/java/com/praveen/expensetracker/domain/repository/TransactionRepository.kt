package com.praveen.expensetracker.domain.repository

import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.CategorySpending
import com.praveen.expensetracker.domain.model.SpendingTrend
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

interface TransactionRepository {
    
    fun getAllTransactions(): Flow<List<Transaction>>
    
    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>
    
    suspend fun getTransactionById(id: String): Transaction?
    
    fun getTransactionByIdFlow(id: String): Flow<Transaction?>
    
    suspend fun insertTransaction(transaction: Transaction)
    
    suspend fun updateTransaction(transaction: Transaction)
    
    suspend fun deleteTransaction(transactionId: String)
    
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>
    
    fun getTransactionsByCategory(category: Category): Flow<List<Transaction>>
    
    fun getTransactionsByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Transaction>>
    
    fun searchTransactions(query: String): Flow<List<Transaction>>
    
    suspend fun getTotalIncome(startDate: LocalDateTime, endDate: LocalDateTime): Double
    
    suspend fun getTotalExpense(startDate: LocalDateTime, endDate: LocalDateTime): Double
    
    suspend fun getCategorySpending(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<CategorySpending>
    
    suspend fun getDailySpendingTrends(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<SpendingTrend>
    
    suspend fun existsBySmsHash(smsHash: String): Boolean
}
