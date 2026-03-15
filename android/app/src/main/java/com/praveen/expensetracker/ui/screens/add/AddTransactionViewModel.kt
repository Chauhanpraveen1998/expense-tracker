package com.praveen.expensetracker.ui.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.AmountChanged -> {
                val sanitizedAmount = sanitizeAmountInput(event.amount)
                _uiState.update { 
                    it.copy(
                        amount = sanitizedAmount,
                        amountError = null
                    )
                }
            }
            
            is AddTransactionEvent.TransactionTypeChanged -> {
                _uiState.update { 
                    it.copy(
                        transactionType = event.type,
                        selectedCategory = null
                    )
                }
            }
            
            is AddTransactionEvent.CategorySelected -> {
                _uiState.update { 
                    it.copy(
                        selectedCategory = event.category,
                        categoryError = null,
                        showCategorySelector = false
                    )
                }
            }
            
            is AddTransactionEvent.MerchantNameChanged -> {
                _uiState.update { 
                    it.copy(
                        merchantName = event.name,
                        merchantError = null
                    )
                }
            }
            
            is AddTransactionEvent.DateSelected -> {
                _uiState.update { 
                    it.copy(
                        selectedDate = event.date,
                        showDatePicker = false
                    )
                }
            }
            
            is AddTransactionEvent.TimeSelected -> {
                _uiState.update { 
                    it.copy(
                        selectedTime = event.time,
                        showTimePicker = false
                    )
                }
            }
            
            is AddTransactionEvent.NoteChanged -> {
                _uiState.update { it.copy(note = event.note) }
            }
            
            is AddTransactionEvent.AccountSelected -> {
                _uiState.update { it.copy(selectedAccountId = event.accountId) }
            }
            
            AddTransactionEvent.ToggleCategorySelector -> {
                _uiState.update { it.copy(showCategorySelector = !it.showCategorySelector) }
            }
            
            AddTransactionEvent.ToggleDatePicker -> {
                _uiState.update { it.copy(showDatePicker = !it.showDatePicker) }
            }
            
            AddTransactionEvent.ToggleTimePicker -> {
                _uiState.update { it.copy(showTimePicker = !it.showTimePicker) }
            }
            
            AddTransactionEvent.SaveTransaction -> {
                saveTransaction()
            }
            
            AddTransactionEvent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun sanitizeAmountInput(input: String): String {
        val sanitized = input.filter { it.isDigit() || it == '.' }
        
        val parts = sanitized.split(".")
        return when {
            parts.size > 2 -> parts[0] + "." + parts[1]
            parts.size == 2 && parts[1].length > 2 -> parts[0] + "." + parts[1].take(2)
            else -> sanitized
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        
        val amount = _uiState.value.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = "Please enter a valid amount") }
            isValid = false
        }
        
        if (_uiState.value.selectedCategory == null) {
            _uiState.update { it.copy(categoryError = "Please select a category") }
            isValid = false
        }
        
        if (_uiState.value.merchantName.isBlank()) {
            _uiState.update { it.copy(merchantError = "Please enter merchant/description") }
            isValid = false
        }
        
        return isValid
    }

    private fun saveTransaction() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val state = _uiState.value
                
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    amount = state.amount.toDouble(),
                    merchantName = state.merchantName.trim(),
                    category = state.selectedCategory!!,
                    type = state.transactionType,
                    dateTime = LocalDateTime.of(state.selectedDate, state.selectedTime),
                    accountId = state.selectedAccountId,
                    note = state.note.takeIf { it.isNotBlank() }?.trim(),
                    merchantLogoUrl = null,
                    isRecurring = false,
                    tags = emptyList()
                )
                
                val result = addTransactionUseCase(transaction)
                
                result.fold(
                    onSuccess = {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSaved = true
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = e.message ?: "Failed to save transaction"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to save transaction"
                    )
                }
            }
        }
    }
}
