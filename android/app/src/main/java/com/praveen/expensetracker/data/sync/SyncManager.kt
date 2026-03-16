package com.praveen.expensetracker.data.sync

import android.util.Log
import com.praveen.expensetracker.data.local.dao.AccountDao
import com.praveen.expensetracker.data.local.dao.TransactionDao
import com.praveen.expensetracker.data.local.entity.SyncStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val networkMonitor: NetworkMonitor
) {
    companion object {
        private const val TAG = "SyncManager"
    }

    data class SyncResult(
        val success: Boolean,
        val transactionsSynced: Int = 0,
        val accountsSynced: Int = 0,
        val errors: List<String> = emptyList()
    )

    suspend fun syncAll(): SyncResult = withContext(Dispatchers.IO) {
        if (!networkMonitor.isCurrentlyConnected()) {
            Log.d(TAG, "No network connection, skipping sync")
            return@withContext SyncResult(success = false, errors = listOf("No network connection"))
        }

        val errors = mutableListOf<String>()
        var transactionsSynced = 0
        var accountsSynced = 0

        try {
            // Push local changes to server
            transactionsSynced += pushPendingTransactions()
            accountsSynced += pushPendingAccounts()

            Log.d(TAG, "Sync completed: $transactionsSynced transactions, $accountsSynced accounts")
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            errors.add(e.message ?: "Unknown error")
        }

        SyncResult(
            success = errors.isEmpty(),
            transactionsSynced = transactionsSynced,
            accountsSynced = accountsSynced,
            errors = errors
        )
    }

    private suspend fun pushPendingTransactions(): Int {
        var count = 0
        val pendingTransactions = transactionDao.getPendingSyncTransactions()
        
        for (transaction in pendingTransactions) {
            try {
                when (transaction.syncStatus) {
                    SyncStatus.PENDING_CREATE -> {
                        // TODO: Call API to create transaction
                        // For now, just mark as synced
                        transactionDao.updateSyncStatus(transaction.id, SyncStatus.SYNCED)
                        count++
                    }
                    SyncStatus.PENDING_UPDATE -> {
                        // TODO: Call API to update transaction
                        transactionDao.updateSyncStatus(transaction.id, SyncStatus.SYNCED)
                        count++
                    }
                    SyncStatus.PENDING_DELETE -> {
                        // TODO: Call API to delete transaction
                        transactionDao.deleteTransactionById(transaction.id)
                        count++
                    }
                    else -> { /* Already synced or conflict */ }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync transaction ${transaction.id}", e)
            }
        }
        
        return count
    }

    private suspend fun pushPendingAccounts(): Int {
        var count = 0
        val pendingAccounts = accountDao.getPendingSyncAccounts()
        
        for (account in pendingAccounts) {
            try {
                when (account.syncStatus) {
                    SyncStatus.PENDING_CREATE -> {
                        // TODO: Call API to create account
                        accountDao.updateSyncStatus(account.id, SyncStatus.SYNCED)
                        count++
                    }
                    SyncStatus.PENDING_UPDATE -> {
                        // TODO: Call API to update account
                        accountDao.updateSyncStatus(account.id, SyncStatus.SYNCED)
                        count++
                    }
                    SyncStatus.PENDING_DELETE -> {
                        // TODO: Call API to delete account
                        accountDao.deleteAccount(account)
                        count++
                    }
                    else -> { /* Already synced */ }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync account ${account.id}", e)
            }
        }
        
        return count
    }

    suspend fun hasPendingChanges(): Boolean {
        val pendingTransactions = transactionDao.getPendingSyncTransactions()
        val pendingAccounts = accountDao.getPendingSyncAccounts()
        return pendingTransactions.isNotEmpty() || pendingAccounts.isNotEmpty()
    }
}
