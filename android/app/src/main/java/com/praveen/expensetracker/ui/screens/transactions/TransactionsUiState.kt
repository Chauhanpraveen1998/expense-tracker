package com.praveen.expensetracker.ui.screens.transactions

import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedFilter: TransactionFilter = TransactionFilter.All,
    val selectedCategories: Set<Category> = emptySet(),
    val allTransactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val groupedTransactions: Map<String, List<Transaction>> = emptyMap(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0
)

sealed class TransactionFilter(val displayName: String) {
    object All : TransactionFilter("All")
    object Income : TransactionFilter("Income")
    object Expense : TransactionFilter("Expense")
    data class ByCategory(val category: Category) : TransactionFilter(category.displayName)
}

sealed class TransactionsEvent {
    data class SearchQueryChanged(val query: String) : TransactionsEvent()
    data class FilterSelected(val filter: TransactionFilter) : TransactionsEvent()
    data class CategoryToggled(val category: Category) : TransactionsEvent()
    object ClearFilters : TransactionsEvent()
    object Refresh : TransactionsEvent()
}
