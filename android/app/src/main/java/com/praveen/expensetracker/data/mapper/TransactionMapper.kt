package com.praveen.expensetracker.data.mapper

import com.praveen.expensetracker.data.local.entity.SyncStatus
import com.praveen.expensetracker.data.local.entity.TransactionEntity
import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType
import java.time.LocalDateTime

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        merchantName = merchantName,
        category = try { Category.valueOf(category) } catch (e: Exception) { Category.OTHER_EXPENSE },
        type = try { TransactionType.valueOf(type) } catch (e: Exception) { TransactionType.EXPENSE },
        dateTime = dateTime,
        accountId = accountId,
        note = note,
        merchantLogoUrl = merchantLogoUrl,
        isRecurring = isRecurring,
        tags = tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    )
}

fun Transaction.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        merchantName = merchantName,
        category = category.name,
        type = type.name,
        dateTime = dateTime,
        accountId = accountId,
        note = note,
        merchantLogoUrl = merchantLogoUrl,
        isRecurring = isRecurring,
        tags = tags.joinToString(","),
        syncStatus = syncStatus,
        lastModified = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}

fun List<TransactionEntity>.toDomainList(): List<Transaction> {
    return map { it.toDomain() }
}
