package com.praveen.expensetracker.ui.screens.transactions

import com.praveen.expensetracker.domain.model.Transaction

data class TransactionDetailUiState(
    val isLoading: Boolean = true,
    val transaction: Transaction? = null,
    val error: String? = null,
    val isDeleted: Boolean = false,
    val showDeleteConfirmation: Boolean = false
)

sealed class TransactionDetailEvent {
    object DeleteTransaction : TransactionDetailEvent()
    object ConfirmDelete : TransactionDetailEvent()
    object DismissDeleteConfirmation : TransactionDetailEvent()
    object ClearError : TransactionDetailEvent()
}
