package com.praveen.expensetracker.ui.screens.dashboard

import com.praveen.expensetracker.domain.model.Insight
import com.praveen.expensetracker.domain.model.SpendingTrend
import com.praveen.expensetracker.domain.model.Transaction

data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val userName: String = "User",
    val greeting: String = "Good Morning",
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val safeToSpend: Double = 0.0,
    val insights: List<Insight> = emptyList(),
    val spendingTrends: List<SpendingTrend> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val groupedTransactions: Map<String, List<Transaction>> = emptyMap()
)
