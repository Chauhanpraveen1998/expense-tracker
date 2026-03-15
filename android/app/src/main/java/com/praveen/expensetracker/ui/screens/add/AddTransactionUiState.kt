package com.praveen.expensetracker.ui.screens.add

import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.TransactionType
import java.time.LocalDate
import java.time.LocalTime

data class AddTransactionUiState(
    val amount: String = "",
    val amountError: String? = null,
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val selectedCategory: Category? = null,
    val categoryError: String? = null,
    val merchantName: String = "",
    val merchantError: String? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.now(),
    val note: String = "",
    val selectedAccountId: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val showCategorySelector: Boolean = false,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false
)

sealed class AddTransactionEvent {
    data class AmountChanged(val amount: String) : AddTransactionEvent()
    data class TransactionTypeChanged(val type: TransactionType) : AddTransactionEvent()
    data class CategorySelected(val category: Category) : AddTransactionEvent()
    data class MerchantNameChanged(val name: String) : AddTransactionEvent()
    data class DateSelected(val date: LocalDate) : AddTransactionEvent()
    data class TimeSelected(val time: LocalTime) : AddTransactionEvent()
    data class NoteChanged(val note: String) : AddTransactionEvent()
    data class AccountSelected(val accountId: String) : AddTransactionEvent()
    object ToggleCategorySelector : AddTransactionEvent()
    object ToggleDatePicker : AddTransactionEvent()
    object ToggleTimePicker : AddTransactionEvent()
    object SaveTransaction : AddTransactionEvent()
    object ClearError : AddTransactionEvent()
}
