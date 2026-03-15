package com.praveen.expensetracker.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType
import com.praveen.expensetracker.domain.repository.TransactionRepository
import com.praveen.expensetracker.domain.usecase.GetDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        observeRecentTransactions()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val dashboardData = getDashboardDataUseCase(monthlyBudget = 50000.0)
                
                val grouped = dashboardData.recentTransactions.groupBy { transaction ->
                    formatDateHeader(transaction.dateTime.toLocalDate())
                }
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = "Praveen",
                        greeting = getGreeting(),
                        totalBudget = dashboardData.totalBudget,
                        totalSpent = dashboardData.totalSpent,
                        safeToSpend = dashboardData.safeToSpend,
                        insights = dashboardData.insights,
                        spendingTrends = dashboardData.spendingTrends,
                        recentTransactions = dashboardData.recentTransactions,
                        groupedTransactions = grouped
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load dashboard data"
                    )
                }
            }
        }
    }

    private fun observeRecentTransactions() {
        viewModelScope.launch {
            transactionRepository.getRecentTransactions(10)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { transactions ->
                    val grouped = transactions.groupBy { transaction ->
                        formatDateHeader(transaction.dateTime.toLocalDate())
                    }
                    
                    val totalSpent = transactions
                        .filter { it.type == TransactionType.EXPENSE }
                        .sumOf { it.amount }
                    
                    _uiState.update {
                        it.copy(
                            recentTransactions = transactions,
                            groupedTransactions = grouped,
                            totalSpent = totalSpent,
                            safeToSpend = (it.totalBudget - totalSpent).coerceAtLeast(0.0)
                        )
                    }
                }
        }
    }

    fun refresh() {
        loadDashboardData()
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    private fun formatDateHeader(date: LocalDate): String {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        
        return when (date) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> date.format(DateTimeFormatter.ofPattern("dd MMMM"))
        }
    }
}
