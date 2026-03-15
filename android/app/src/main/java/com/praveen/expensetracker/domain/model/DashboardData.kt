package com.praveen.expensetracker.domain.model

data class DashboardData(
    val totalBudget: Double,
    val totalSpent: Double,
    val totalIncome: Double,
    val safeToSpend: Double,
    val budgetUsedPercentage: Float,
    val insights: List<Insight>,
    val spendingTrends: List<SpendingTrend>,
    val recentTransactions: List<Transaction>,
    val topCategories: List<CategorySpending>
)

data class CategorySpending(
    val category: Category,
    val amount: Double,
    val percentage: Float,
    val transactionCount: Int
)
