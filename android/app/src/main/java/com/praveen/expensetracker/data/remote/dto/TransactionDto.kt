package com.praveen.expensetracker.data.remote.dto

import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TransactionRequest(
    val amount: Double,
    val transactionType: String,
    val merchantName: String,
    val categoryId: String? = null,
    val accountId: String? = null,
    val date: String,
    val description: String? = null,
    val note: String? = null,
    val merchantLogoUrl: String? = null,
    val isRecurring: Boolean = false,
    val tags: List<String> = emptyList()
) {
    companion object {
        fun fromDomain(transaction: Transaction): TransactionRequest {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            return TransactionRequest(
                amount = transaction.amount,
                transactionType = transaction.type.name,
                merchantName = transaction.merchantName,
                categoryId = null,
                accountId = transaction.accountId,
                date = transaction.dateTime.format(formatter),
                description = transaction.note,
                note = transaction.note,
                merchantLogoUrl = transaction.merchantLogoUrl,
                isRecurring = transaction.isRecurring,
                tags = transaction.tags
            )
        }
    }
}

data class TransactionResponse(
    val id: String,
    val amount: Double,
    val transactionType: String,
    val merchantName: String,
    val date: String,
    val description: String? = null,
    val note: String? = null,
    val merchantLogoUrl: String? = null,
    val isRecurring: Boolean = false,
    val tags: List<String>? = null,
    val categoryId: String? = null,
    val categoryName: String? = null,
    val accountId: String? = null,
    val accountName: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    fun toDomain(): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            merchantName = merchantName,
            category = mapCategory(categoryName),
            type = if (transactionType == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE,
            dateTime = parseDateTime(date),
            accountId = accountId,
            note = note ?: description,
            merchantLogoUrl = merchantLogoUrl,
            isRecurring = isRecurring,
            tags = tags ?: emptyList()
        )
    }
    
    private fun mapCategory(name: String?): Category {
        if (name == null) return Category.OTHER_EXPENSE
        return Category.entries.find { 
            it.displayName.equals(name, ignoreCase = true) 
        } ?: Category.OTHER_EXPENSE
    }
    
    private fun parseDateTime(dateStr: String): LocalDateTime {
        return try {
            LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            try {
                LocalDateTime.parse(dateStr.substringBefore("+").substringBefore("Z"))
            } catch (e2: Exception) {
                LocalDateTime.now()
            }
        }
    }
}

data class PagedResponse<T>(
    val content: List<T>,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val size: Int = 20,
    val number: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true
)
