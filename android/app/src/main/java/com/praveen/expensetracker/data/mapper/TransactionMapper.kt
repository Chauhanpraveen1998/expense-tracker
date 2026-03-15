package com.praveen.expensetracker.data.mapper

import com.praveen.expensetracker.data.local.entity.TransactionEntity
import com.praveen.expensetracker.domain.model.Transaction
import java.time.LocalDateTime

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        merchantName = merchantName,
        category = category,
        type = type,
        dateTime = dateTime,
        accountId = accountId,
        note = note,
        merchantLogoUrl = merchantLogoUrl,
        isRecurring = isRecurring,
        tags = tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        merchantName = merchantName,
        category = category,
        type = type,
        dateTime = dateTime,
        accountId = accountId,
        note = note,
        merchantLogoUrl = merchantLogoUrl,
        isRecurring = isRecurring,
        tags = tags.joinToString(","),
        updatedAt = LocalDateTime.now()
    )
}

fun List<TransactionEntity>.toDomainList(): List<Transaction> {
    return map { it.toDomain() }
}
