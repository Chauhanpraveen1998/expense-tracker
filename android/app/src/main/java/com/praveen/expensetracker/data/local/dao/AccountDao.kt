package com.praveen.expensetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.praveen.expensetracker.data.local.entity.AccountEntity
import com.praveen.expensetracker.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)
    
    @Update
    suspend fun updateAccount(account: AccountEntity)
    
    @Delete
    suspend fun deleteAccount(account: AccountEntity)
    
    @Query("SELECT * FROM accounts WHERE is_active = 1 ORDER BY created_at DESC")
    fun getAllActiveAccounts(): Flow<List<AccountEntity>>
    
    @Query("SELECT * FROM accounts ORDER BY created_at DESC")
    fun getAllAccounts(): Flow<List<AccountEntity>>
    
    @Query("SELECT * FROM accounts WHERE id = :accountId")
    suspend fun getAccountById(accountId: String): AccountEntity?
    
    @Query("SELECT SUM(balance) FROM accounts WHERE is_active = 1")
    fun getTotalBalance(): Flow<Double?>
    
    @Query("SELECT COUNT(*) FROM accounts WHERE is_active = 1")
    fun getActiveAccountCount(): Flow<Int>
    
    @Query("UPDATE accounts SET balance = balance + :amount WHERE id = :accountId")
    suspend fun updateBalance(accountId: String, amount: Double)
    
    @Query("UPDATE accounts SET balance = :balance WHERE id = :accountId")
    suspend fun updateAccountBalance(accountId: String, balance: Double)
    
    // Sync queries
    @Query("SELECT * FROM accounts WHERE sync_status != 'SYNCED'")
    suspend fun getPendingSyncAccounts(): List<AccountEntity>

    @Query("UPDATE accounts SET sync_status = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)

    @Query("UPDATE accounts SET sync_status = :status, remote_id = :remoteId WHERE id = :id")
    suspend fun updateSyncStatusWithRemoteId(id: String, status: SyncStatus, remoteId: String)

    @Query("DELETE FROM accounts WHERE sync_status = 'SYNCED'")
    suspend fun deleteAllSyncedAccounts()
}
