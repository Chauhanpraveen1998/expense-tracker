package com.praveen.expensetracker.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praveen.expensetracker.domain.model.Category
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType
import com.praveen.expensetracker.domain.repository.TransactionRepository
import com.praveen.expensetracker.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private val selectedFilter = MutableStateFlow<TransactionFilter>(TransactionFilter.All)
    private val selectedCategories = MutableStateFlow<Set<Category>>(emptySet())

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                searchQuery.debounce(300),
                selectedFilter,
                selectedCategories,
                getTransactionsUseCase.getAllTransactions()
            ) { query, filter, categories, allTransactions ->
                FilteredData(query, filter, categories, allTransactions)
            }
            .catch { e ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load transactions"
                    )
                }
            }
            .collect { data ->
                val filtered = applyFilters(
                    transactions = data.transactions,
                    query = data.query,
                    filter = data.filter,
                    categories = data.categories
                )
                
                val totalIncome = filtered
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }
                
                val totalExpense = filtered
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }
                
                val grouped = filtered
                    .sortedByDescending { it.dateTime }
                    .groupBy { transaction ->
                        formatDateHeader(transaction.dateTime.toLocalDate())
                    }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allTransactions = data.transactions,
                        filteredTransactions = filtered,
                        groupedTransactions = grouped,
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        searchQuery = data.query,
                        selectedFilter = data.filter,
                        selectedCategories = data.categories
                    )
                }
            }
        }
    }

    fun onEvent(event: TransactionsEvent) {
        when (event) {
            is TransactionsEvent.SearchQueryChanged -> {
                searchQuery.value = event.query
                _uiState.update { it.copy(searchQuery = event.query) }
            }
            
            is TransactionsEvent.FilterSelected -> {
                selectedFilter.value = event.filter
                _uiState.update { it.copy(selectedFilter = event.filter) }
            }
            
            is TransactionsEvent.CategoryToggled -> {
                val currentCategories = selectedCategories.value.toMutableSet()
                if (currentCategories.contains(event.category)) {
                    currentCategories.remove(event.category)
                } else {
                    currentCategories.add(event.category)
                }
                selectedCategories.value = currentCategories
                _uiState.update { it.copy(selectedCategories = currentCategories) }
            }
            
            is TransactionsEvent.ClearFilters -> {
                searchQuery.value = ""
                selectedFilter.value = TransactionFilter.All
                selectedCategories.value = emptySet()
                _uiState.update { 
                    it.copy(
                        searchQuery = "",
                        selectedFilter = TransactionFilter.All,
                        selectedCategories = emptySet()
                    )
                }
            }
            
            is TransactionsEvent.Refresh -> {
                observeTransactions()
            }
        }
    }

    private fun applyFilters(
        transactions: List<Transaction>,
        query: String,
        filter: TransactionFilter,
        categories: Set<Category>
    ): List<Transaction> {
        var filtered = transactions

        if (query.isNotBlank()) {
            val searchLower = query.lowercase()
            filtered = filtered.filter { transaction ->
                transaction.merchantName.lowercase().contains(searchLower) ||
                transaction.category.displayName.lowercase().contains(searchLower) ||
                transaction.note?.lowercase()?.contains(searchLower) == true
            }
        }

        filtered = when (filter) {
            is TransactionFilter.All -> filtered
            is TransactionFilter.Income -> filtered.filter { it.type == TransactionType.INCOME }
            is TransactionFilter.Expense -> filtered.filter { it.type == TransactionType.EXPENSE }
            is TransactionFilter.ByCategory -> filtered.filter { it.category == filter.category }
        }

        if (categories.isNotEmpty()) {
            filtered = filtered.filter { it.category in categories }
        }

        return filtered.sortedByDescending { it.dateTime }
    }

    private fun formatDateHeader(date: LocalDate): String {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val thisWeekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        
        return when {
            date == today -> "Today"
            date == yesterday -> "Yesterday"
            date >= thisWeekStart -> date.format(DateTimeFormatter.ofPattern("EEEE"))
            date.year == today.year -> date.format(DateTimeFormatter.ofPattern("dd MMMM"))
            else -> date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        }
    }

    private data class FilteredData(
        val query: String,
        val filter: TransactionFilter,
        val categories: Set<Category>,
        val transactions: List<Transaction>
    )
}
