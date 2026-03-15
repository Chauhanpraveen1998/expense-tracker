package com.praveen.expensetracker.domain.repository

import com.praveen.expensetracker.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllAccounts(): Flow<List<Account>>
    fun getActiveAccounts(): Flow<List<Account>>
    suspend fun getAccountById(id: String): Account?
    suspend fun insertAccount(account: Account)
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(accountId: String)
    fun getTotalBalance(): Flow<Double>
    suspend fun updateBalance(accountId: String, amount: Double)
}
