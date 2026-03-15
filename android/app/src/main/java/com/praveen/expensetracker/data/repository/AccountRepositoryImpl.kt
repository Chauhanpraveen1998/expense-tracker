package com.praveen.expensetracker.data.repository

import androidx.compose.ui.graphics.Color
import com.praveen.expensetracker.data.local.dao.AccountDao
import com.praveen.expensetracker.data.local.entity.AccountEntity
import com.praveen.expensetracker.domain.model.Account
import com.praveen.expensetracker.domain.model.AccountType
import com.praveen.expensetracker.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveAccounts(): Flow<List<Account>> {
        return accountDao.getAllActiveAccounts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAccountById(id: String): Account? {
        return accountDao.getAccountById(id)?.toDomain()
    }

    override suspend fun insertAccount(account: Account) {
        accountDao.insertAccount(account.toEntity())
    }

    override suspend fun updateAccount(account: Account) {
        accountDao.updateAccount(account.toEntity())
    }

    override suspend fun deleteAccount(accountId: String) {
        accountDao.getAccountById(accountId)?.let {
            accountDao.deleteAccount(it)
        }
    }

    override fun getTotalBalance(): Flow<Double> {
        return accountDao.getTotalBalance().map { it ?: 0.0 }
    }

    override suspend fun updateBalance(accountId: String, amount: Double) {
        accountDao.updateBalance(accountId, amount)
    }

    private fun AccountEntity.toDomain(): Account {
        return Account(
            id = id,
            name = name,
            type = AccountType.valueOf(type),
            balance = balance,
            bankName = bankName,
            lastFourDigits = lastFourDigits,
            colorPrimary = colorPrimary?.let { parseColor(it) }
                ?: Color(0xFF1E3A8A),
            colorSecondary = colorSecondary?.let { parseColor(it) }
                ?: Color(0xFF3B82F6),
            isActive = isActive
        )
    }

    private fun Account.toEntity(): AccountEntity {
        return AccountEntity(
            id = id,
            name = name,
            type = type.name,
            balance = balance,
            bankName = bankName,
            lastFourDigits = lastFourDigits,
            colorPrimary = colorToHex(colorPrimary),
            colorSecondary = colorToHex(colorSecondary),
            isActive = isActive,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    private fun parseColor(hex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            Color(0xFF1E3A8A)
        }
    }

    private fun colorToHex(color: Color): String {
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()
        return String.format("#%02X%02X%02X", red, green, blue)
    }
}
