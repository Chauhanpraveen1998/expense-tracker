package com.praveen.expensetracker.data.remote.dto

import androidx.compose.ui.graphics.Color
import com.praveen.expensetracker.domain.model.Account
import com.praveen.expensetracker.domain.model.AccountType

data class AccountRequest(
    val name: String,
    val type: String,
    val balance: Double = 0.0,
    val bankName: String? = null,
    val lastFourDigits: String? = null,
    val colorPrimary: String = "#1E3A8A",
    val colorSecondary: String = "#3B82F6"
) {
    companion object {
        fun fromDomain(account: Account): AccountRequest {
            return AccountRequest(
                name = account.name,
                type = account.type.name,
                balance = account.balance,
                bankName = account.bankName,
                lastFourDigits = account.lastFourDigits,
                colorPrimary = "#1E3A8A",
                colorSecondary = "#3B82F6"
            )
        }
    }
}

data class AccountResponse(
    val id: String = "",
    val name: String = "",
    val type: String = "BANK",
    val typeDisplayName: String? = null,
    val balance: Double = 0.0,
    val bankName: String? = null,
    val lastFourDigits: String? = null,
    val colorPrimary: String? = null,
    val colorSecondary: String? = null,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    fun toDomain(): Account {
        return Account(
            id = id,
            name = name,
            type = mapAccountType(type),
            balance = balance,
            bankName = bankName,
            lastFourDigits = lastFourDigits,
            colorPrimary = safeParseColor(colorPrimary, Color(0xFF1E3A8A)),
            colorSecondary = safeParseColor(colorSecondary, Color(0xFF3B82F6)),
            isActive = isActive
        )
    }
    
    private fun mapAccountType(type: String): AccountType {
        return when (type.uppercase()) {
            "BANK" -> AccountType.BANK
            "CREDIT_CARD" -> AccountType.CREDIT_CARD
            "WALLET" -> AccountType.WALLET
            "CASH" -> AccountType.CASH
            else -> AccountType.BANK
        }
    }
    
    private fun safeParseColor(colorStr: String?, default: Color): Color {
        if (colorStr.isNullOrBlank()) return default
        return try {
            val cleanColor = if (colorStr.startsWith("#")) colorStr else "#$colorStr"
            Color(android.graphics.Color.parseColor(cleanColor))
        } catch (e: Exception) {
            default
        }
    }
}

data class AccountSummaryResponse(
    val totalBalance: Double = 0.0,
    val accountCount: Long = 0,
    val accounts: List<AccountResponse>? = null
)
