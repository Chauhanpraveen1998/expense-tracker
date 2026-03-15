package com.praveen.expensetracker.ui.screens.accounts

import com.praveen.expensetracker.domain.model.Account
import com.praveen.expensetracker.domain.model.AccountType

data class AccountsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val accounts: List<Account> = emptyList(),
    val totalBalance: Double = 0.0,
    val showAddAccountDialog: Boolean = false,
    val editingAccount: Account? = null
)

data class AddAccountState(
    val name: String = "",
    val type: AccountType = AccountType.BANK,
    val balance: String = "",
    val bankName: String = "",
    val lastFourDigits: String = "",
    val nameError: String? = null,
    val balanceError: String? = null
)

sealed class AccountsEvent {
    object Refresh : AccountsEvent()
    object ShowAddAccountDialog : AccountsEvent()
    object DismissAddAccountDialog : AccountsEvent()
    data class EditAccount(val account: Account) : AccountsEvent()
    data class DeleteAccount(val accountId: String) : AccountsEvent()
    data class SaveAccount(val state: AddAccountState) : AccountsEvent()
}
