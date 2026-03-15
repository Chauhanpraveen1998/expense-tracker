package com.praveen.expensetracker.domain.usecase

import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        return try {
            require(transaction.amount > 0) { "Amount must be greater than 0" }
            require(transaction.merchantName.isNotBlank()) { "Merchant name is required" }
            
            repository.insertTransaction(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
