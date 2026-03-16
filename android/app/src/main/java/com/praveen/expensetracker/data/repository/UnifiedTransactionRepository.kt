package com.praveen.expensetracker.data.repository

import com.praveen.expensetracker.data.local.dao.TransactionDao
import com.praveen.expensetracker.data.local.entity.SyncStatus
import com.praveen.expensetracker.data.mapper.toDomain
import com.praveen.expensetracker.data.mapper.toEntity
import com.praveen.expensetracker.data.sync.NetworkMonitor
import com.praveen.expensetracker.data.sync.SyncManager
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Result

@Singleton
class UnifiedTransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager
) {
    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getRecentTransactions(limit: Int = 5): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getTransactionsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun searchTransactions(query: String): Flow<List<Transaction>> {
        return transactionDao.searchTransactions(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getTransactionById(id: String): Transaction? {
        return transactionDao.getTransactionById(id)?.toDomain()
    }

    suspend fun insertTransaction(transaction: Transaction): Result<Unit> {
        return try {
            val entity = transaction.copy(
                id = transaction.id.ifEmpty { UUID.randomUUID().toString() }
            ).toEntity().copy(
                syncStatus = SyncStatus.PENDING_CREATE
            )

            transactionDao.insertTransaction(entity)

            if (networkMonitor.isCurrentlyConnected()) {
                try {
                    syncManager.syncAll()
                } catch (e: Exception) {
                    // Will sync later via WorkManager
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTransaction(transaction: Transaction): Result<Unit> {
        return try {
            val existingEntity = transactionDao.getTransactionById(transaction.id)
            val entity = transaction.toEntity().copy(
                syncStatus = SyncStatus.PENDING_UPDATE,
                remoteId = existingEntity?.remoteId
            )

            transactionDao.updateTransaction(entity)

            if (networkMonitor.isCurrentlyConnected()) {
                try {
                    syncManager.syncAll()
                } catch (e: Exception) {
                    // Will sync later via WorkManager
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(id: String): Result<Unit> {
        return try {
            val entity = transactionDao.getTransactionById(id)
            if (entity != null) {
                if (entity.remoteId != null) {
                    transactionDao.updateSyncStatus(id, SyncStatus.PENDING_DELETE)

                    if (networkMonitor.isCurrentlyConnected()) {
                        syncManager.syncAll()
                    }
                } else {
                    transactionDao.deleteTransactionById(id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshFromRemote(): Result<Unit> {
        return try {
            syncManager.syncAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun hasPendingChanges(): Boolean {
        return syncManager.hasPendingChanges()
    }

    suspend fun getTotalByType(type: TransactionType, startDate: LocalDateTime, endDate: LocalDateTime): Double {
        return transactionDao.getTotalByTypeAndDateRange(type.name, startDate, endDate) ?: 0.0
    }

    suspend fun existsBySmsHash(smsHash: String): Boolean {
        return transactionDao.existsBySmsHash(smsHash)
    }
}
