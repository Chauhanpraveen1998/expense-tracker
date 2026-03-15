package com.praveen.expensetracker.ui.screens.analytics

import com.praveen.expensetracker.domain.model.CategorySpending
import com.praveen.expensetracker.domain.model.MerchantSpending
import com.praveen.expensetracker.domain.model.MonthlySpending
import com.praveen.expensetracker.domain.model.SpendingTrend
import java.time.LocalDate
import java.time.YearMonth

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedDateRange: DateRangeOption = DateRangeOption.THIS_MONTH,
    val customStartDate: LocalDate? = null,
    val customEndDate: LocalDate? = null,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netSavings: Double = 0.0,
    val categoryBreakdown: List<CategorySpending> = emptyList(),
    val dailySpending: List<SpendingTrend> = emptyList(),
    val monthlyComparison: List<MonthlySpending> = emptyList(),
    val topMerchants: List<MerchantSpending> = emptyList()
)

enum class DateRangeOption(val label: String) {
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    LAST_3_MONTHS("3 Months"),
    THIS_YEAR("This Year"),
    CUSTOM("Custom")
}

sealed class AnalyticsEvent {
    data class DateRangeSelected(val option: DateRangeOption) : AnalyticsEvent()
    data class CustomDateRangeSelected(val start: LocalDate, val end: LocalDate) : AnalyticsEvent()
    object Refresh : AnalyticsEvent()
}
