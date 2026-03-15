package com.praveen.expensetracker.domain.usecase

import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType
import com.praveen.expensetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun getAllTransactions(): Flow<List<Transaction>> {
        return repository.getAllTransactions()
    }
    
    fun getRecentTransactions(limit: Int = 10): Flow<List<Transaction>> {
        return repository.getRecentTransactions(limit)
    }
    
    fun getByType(type: TransactionType): Flow<List<Transaction>> {
        return repository.getTransactionsByType(type)
    }
    
    fun getByCategory(category: Category): Flow<List<Transaction>> {
        return repository.getTransactionsByCategory(category)
    }
    
    fun getByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Transaction>> {
        return repository.getTransactionsByDateRange(startDate, endDate)
    }
    
    fun search(query: String): Flow<List<Transaction>> {
        return repository.searchTransactions(query)
    }
}
