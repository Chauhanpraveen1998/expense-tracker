package com.praveen.expensetracker.domain.model

data class DashboardData(
    val totalBudget: Double,
    val totalSpent: Double,
    val totalIncome: Double,
    val safeToSpend: Double,
    val budgetUsedPercentage: Double = 0.0,
    val insights: List<Insight> = emptyList(),
    val spendingTrends: List<SpendingTrend> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val topCategories: List<CategorySpending> = emptyList()
)

data class CategorySpending(
    val category: Category,
    val amount: Double,
    val percentage: Float,
    val transactionCount: Int = 0
)
