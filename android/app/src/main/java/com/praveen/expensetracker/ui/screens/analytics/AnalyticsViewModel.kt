package com.praveen.expensetracker.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praveen.expensetracker.domain.model.DateRange
import com.praveen.expensetracker.domain.usecase.GetAnalyticsDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getAnalyticsDataUseCase: GetAnalyticsDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun onEvent(event: AnalyticsEvent) {
        when (event) {
            is AnalyticsEvent.DateRangeSelected -> {
                _uiState.update { it.copy(selectedDateRange = event.option) }
                loadAnalytics()
            }
            is AnalyticsEvent.CustomDateRangeSelected -> {
                _uiState.update {
                    it.copy(
                        selectedDateRange = DateRangeOption.CUSTOM,
                        customStartDate = event.start,
                        customEndDate = event.end
                    )
                }
                loadAnalytics()
            }
            AnalyticsEvent.Refresh -> {
                loadAnalytics()
            }
        }
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val dateRange = getDateRange()
                val analyticsData = getAnalyticsDataUseCase(dateRange)
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalIncome = analyticsData.totalIncome,
                        totalExpense = analyticsData.totalExpense,
                        netSavings = analyticsData.netSavings,
                        categoryBreakdown = analyticsData.categoryBreakdown,
                        dailySpending = analyticsData.dailySpending,
                        monthlyComparison = analyticsData.monthlyComparison,
                        topMerchants = analyticsData.topMerchants
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load analytics"
                    )
                }
            }
        }
    }

    private fun getDateRange(): DateRange {
        val state = _uiState.value
        val today = LocalDate.now()
        
        return when (state.selectedDateRange) {
            DateRangeOption.THIS_WEEK -> {
                val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                DateRange(startOfWeek, today, "This Week")
            }
            DateRangeOption.THIS_MONTH -> {
                val startOfMonth = today.withDayOfMonth(1)
                DateRange(startOfMonth, today, "This Month")
            }
            DateRangeOption.LAST_MONTH -> {
                val lastMonth = YearMonth.now().minusMonths(1)
                DateRange(
                    lastMonth.atDay(1),
                    lastMonth.atEndOfMonth(),
                    "Last Month"
                )
            }
            DateRangeOption.LAST_3_MONTHS -> {
                val threeMonthsAgo = today.minusMonths(3)
                DateRange(threeMonthsAgo, today, "Last 3 Months")
            }
            DateRangeOption.THIS_YEAR -> {
                val startOfYear = today.withDayOfYear(1)
                DateRange(startOfYear, today, "This Year")
            }
            DateRangeOption.CUSTOM -> {
                DateRange(
                    state.customStartDate ?: today.minusMonths(1),
                    state.customEndDate ?: today,
                    "Custom"
                )
            }
        }
    }
}
