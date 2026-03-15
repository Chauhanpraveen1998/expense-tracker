package com.praveen.expensetracker.domain.usecase

import com.praveen.expensetracker.data.sms.MerchantCategorizer
import com.praveen.expensetracker.data.sms.SmsBankPatterns
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.repository.TransactionRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class ProcessSmsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(smsBody: String, sender: String): Result<Transaction?> {
        return try {
            val parsed = SmsBankPatterns.parse(smsBody, sender)
                ?: return Result.success(null)
            
            if (transactionRepository.existsBySmsHash(parsed.smsHash)) {
                return Result.success(null)
            }
            
            val category = MerchantCategorizer.categorize(parsed.merchantName)
            
            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                amount = parsed.amount,
                merchantName = parsed.merchantName ?: "Unknown",
                category = category,
                type = parsed.type,
                dateTime = LocalDateTime.now(),
                accountId = null,
                note = null,
                merchantLogoUrl = null,
                isRecurring = false,
                tags = listOf("auto-detected")
            )
            
            transactionRepository.insertTransaction(transaction)
            
            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
