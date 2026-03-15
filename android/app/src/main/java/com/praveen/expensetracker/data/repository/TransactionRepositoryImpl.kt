package com.praveen.expensetracker.data.repository

import com.praveen.expensetracker.data.local.dao.TransactionDao
import com.praveen.expensetracker.data.mapper.toDomain
import com.praveen.expensetracker.data.mapper.toDomainList
import com.praveen.expensetracker.data.mapper.toEntity
import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.CategorySpending
import com.praveen.expensetracker.domain.model.SpendingTrend
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType
import com.praveen.expensetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    
    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.toDomainList()
        }
    }
    
    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions(limit).map { entities ->
            entities.toDomainList()
        }
    }
    
    override suspend fun getTransactionById(id: String): Transaction? {
        return transactionDao.getTransactionById(id)?.toDomain()
    }
    
    override fun getTransactionByIdFlow(id: String): Flow<Transaction?> {
        return transactionDao.getTransactionByIdFlow(id).map { entity ->
            entity?.toDomain()
        }
    }
    
    override suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }
    
    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }
    
    override suspend fun deleteTransaction(transactionId: String) {
        transactionDao.deleteTransactionById(transactionId)
    }
    
    override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type).map { entities ->
            entities.toDomainList()
        }
    }
    
    override fun getTransactionsByCategory(category: Category): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByCategory(category).map { entities ->
            entities.toDomainList()
        }
    }
    
    override fun getTransactionsByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate).map { entities ->
            entities.toDomainList()
        }
    }
    
    override fun searchTransactions(query: String): Flow<List<Transaction>> {
        return transactionDao.searchTransactions(query).map { entities ->
            entities.toDomainList()
        }
    }
    
    override suspend fun getTotalIncome(startDate: LocalDateTime, endDate: LocalDateTime): Double {
        return transactionDao.getTotalByTypeAndDateRange(
            TransactionType.INCOME,
            startDate,
            endDate
        ) ?: 0.0
    }
    
    override suspend fun getTotalExpense(startDate: LocalDateTime, endDate: LocalDateTime): Double {
        return transactionDao.getTotalByTypeAndDateRange(
            TransactionType.EXPENSE,
            startDate,
            endDate
        ) ?: 0.0
    }
    
    override suspend fun getCategorySpending(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<CategorySpending> {
        val summary = transactionDao.getCategorySpendingSummary(startDate, endDate)
        val total = summary.sumOf { it.total }
        
        return summary.map { tuple ->
            CategorySpending(
                category = tuple.category,
                amount = tuple.total,
                percentage = if (total > 0) (tuple.total / total * 100).toFloat() else 0f,
                transactionCount = 0
            )
        }
    }
    
    override suspend fun getDailySpendingTrends(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<SpendingTrend> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(LocalTime.MAX)
        
        val dailyData = transactionDao.getDailySpending(startDateTime, endDateTime)
        
        return dailyData.map { tuple ->
            SpendingTrend(
                date = LocalDate.parse(tuple.date),
                amount = tuple.total
            )
        }
    }
    
    override suspend fun existsBySmsHash(smsHash: String): Boolean {
        return transactionDao.existsBySmsHash(smsHash)
    }
}
