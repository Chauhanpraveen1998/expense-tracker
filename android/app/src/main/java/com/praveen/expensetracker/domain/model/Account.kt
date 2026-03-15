package com.praveen.expensetracker.domain.model

import androidx.compose.ui.graphics.Color

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val bankName: String? = null,
    val lastFourDigits: String? = null,
    val colorPrimary: Color = Color(0xFF1E3A8A),
    val colorSecondary: Color = Color(0xFF3B82F6),
    val isActive: Boolean = true
)

enum class AccountType(val displayName: String) {
    BANK("Bank Account"),
    CREDIT_CARD("Credit Card"),
    WALLET("Wallet"),
    CASH("Cash")
}
