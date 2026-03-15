package com.praveen.expensetracker.domain.model

import java.time.LocalDate
import java.time.YearMonth

data class AnalyticsData(
    val dateRange: DateRange,
    val totalIncome: Double,
    val totalExpense: Double,
    val netSavings: Double,
    val categoryBreakdown: List<CategorySpending>,
    val dailySpending: List<SpendingTrend>,
    val monthlyComparison: List<MonthlySpending>,
    val topMerchants: List<MerchantSpending>
)

data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val label: String
)

data class MonthlySpending(
    val month: YearMonth,
    val income: Double,
    val expense: Double
)

data class MerchantSpending(
    val merchantName: String,
    val totalAmount: Double,
    val transactionCount: Int,
    val category: Category
)
