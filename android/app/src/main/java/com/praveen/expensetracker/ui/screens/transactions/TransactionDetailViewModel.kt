package com.praveen.expensetracker.ui.screens.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praveen.expensetracker.domain.repository.TransactionRepository
import com.praveen.expensetracker.domain.usecase.DeleteTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val transactionId: String = checkNotNull(savedStateHandle["transactionId"])

    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState.asStateFlow()

    init {
        loadTransaction()
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            transactionRepository.getTransactionByIdFlow(transactionId)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load transaction"
                        )
                    }
                }
                .collect { transaction ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            transaction = transaction,
                            error = if (transaction == null) "Transaction not found" else null
                        )
                    }
                }
        }
    }

    fun onEvent(event: TransactionDetailEvent) {
        when (event) {
            TransactionDetailEvent.DeleteTransaction -> {
                _uiState.update { it.copy(showDeleteConfirmation = true) }
            }
            
            TransactionDetailEvent.ConfirmDelete -> {
                deleteTransaction()
            }
            
            TransactionDetailEvent.DismissDeleteConfirmation -> {
                _uiState.update { it.copy(showDeleteConfirmation = false) }
            }
            
            TransactionDetailEvent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun deleteTransaction() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showDeleteConfirmation = false) }
            
            val result = deleteTransactionUseCase(transactionId)
            
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isDeleted = true
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to delete transaction"
                        )
                    }
                }
            )
        }
    }
}
