package com.praveen.expensetracker.ui.screens.accounts

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praveen.expensetracker.domain.model.Account
import com.praveen.expensetracker.domain.model.AccountType
import com.praveen.expensetracker.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        observeAccounts()
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            combine(
                accountRepository.getAllAccounts(),
                accountRepository.getTotalBalance()
            ) { accounts, totalBalance ->
                Pair(accounts, totalBalance)
            }
            .catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load accounts"
                    )
                }
            }
            .collect { (accounts, totalBalance) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        accounts = accounts,
                        totalBalance = totalBalance
                    )
                }
            }
        }
    }

    fun onEvent(event: AccountsEvent) {
        when (event) {
            AccountsEvent.Refresh -> {
                _uiState.update { it.copy(isLoading = true) }
                observeAccounts()
            }
            
            AccountsEvent.ShowAddAccountDialog -> {
                _uiState.update { it.copy(showAddAccountDialog = true, editingAccount = null) }
            }
            
            AccountsEvent.DismissAddAccountDialog -> {
                _uiState.update { it.copy(showAddAccountDialog = false, editingAccount = null) }
            }
            
            is AccountsEvent.EditAccount -> {
                _uiState.update { 
                    it.copy(showAddAccountDialog = true, editingAccount = event.account) 
                }
            }
            
            is AccountsEvent.DeleteAccount -> {
                deleteAccount(event.accountId)
            }
            
            is AccountsEvent.SaveAccount -> {
                saveAccount(event.state)
            }
        }
    }

    private fun saveAccount(state: AddAccountState) {
        val balance = state.balance.toDoubleOrNull()
        if (balance == null) {
            return
        }

        viewModelScope.launch {
            val editingAccount = _uiState.value.editingAccount
            val colors = getAccountColors(state.type)
            
            val account = Account(
                id = editingAccount?.id ?: UUID.randomUUID().toString(),
                name = state.name.trim(),
                type = state.type,
                balance = balance,
                bankName = state.bankName.takeIf { it.isNotBlank() }?.trim(),
                lastFourDigits = state.lastFourDigits.takeIf { it.isNotBlank() }?.trim(),
                colorPrimary = colors.first,
                colorSecondary = colors.second,
                isActive = true
            )

            if (editingAccount != null) {
                accountRepository.updateAccount(account)
            } else {
                accountRepository.insertAccount(account)
            }

            _uiState.update { it.copy(showAddAccountDialog = false, editingAccount = null) }
        }
    }

    private fun deleteAccount(accountId: String) {
        viewModelScope.launch {
            accountRepository.deleteAccount(accountId)
        }
    }

    private fun getAccountColors(type: AccountType): Pair<Color, Color> {
        return when (type) {
            AccountType.BANK -> Pair(Color(0xFF1E3A8A), Color(0xFF3B82F6))
            AccountType.CREDIT_CARD -> Pair(Color(0xFF7C3AED), Color(0xFFA78BFA))
            AccountType.WALLET -> Pair(Color(0xFF059669), Color(0xFF34D399))
            AccountType.CASH -> Pair(Color(0xFFD97706), Color(0xFFFBBF24))
        }
    }
}
