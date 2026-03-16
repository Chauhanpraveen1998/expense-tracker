package com.praveen.expensetracker.data.remote.dto

import com.praveen.expensetracker.domain.model.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class DashboardResponseDto(
    val totalBudget: Double = 50000.0,
    val totalSpent: Double = 0.0,
    val totalIncome: Double = 0.0,
    val safeToSpend: Double = 0.0,
    val budgetUsedPercentage: Double = 0.0,
    val insights: List<InsightDto>? = null,
    val spendingTrends: List<SpendingTrendDto>? = null,
    val recentTransactions: List<TransactionResponse>? = null,
    val topCategories: List<CategorySpendingDto>? = null,
    val accountSummary: AccountSummaryResponse? = null
) {
    fun toDomain(): DashboardData {
        return DashboardData(
            totalBudget = totalBudget,
            totalSpent = totalSpent,
            totalIncome = totalIncome,
            safeToSpend = safeToSpend,
            budgetUsedPercentage = budgetUsedPercentage,
            insights = insights?.map { it.toDomain() } ?: emptyList(),
            spendingTrends = spendingTrends?.map { it.toDomain() } ?: emptyList(),
            recentTransactions = recentTransactions?.map { it.toDomain() } ?: emptyList(),
            topCategories = topCategories?.map { it.toDomain() } ?: emptyList()
        )
    }
}

data class InsightDto(
    val id: String = "",
    val type: String = "INFO",
    val title: String = "",
    val description: String = "",
    val actionText: String? = null,
    val relatedCategory: String? = null
) {
    fun toDomain(): Insight {
        val category = relatedCategory?.let { catName ->
            Category.entries.find { 
                it.displayName.equals(catName, ignoreCase = true) 
            }
        }
        return Insight(
            id = id,
            type = when (type.uppercase()) {
                "TIP" -> InsightType.TIP
                "WARNING" -> InsightType.WARNING
                "INFO" -> InsightType.INFO
                "TREND" -> InsightType.TREND
                else -> InsightType.INFO
            },
            title = title,
            description = description,
            actionText = actionText,
            relatedCategory = category
        )
    }
}

data class SpendingTrendDto(
    val date: String = "",
    val amount: Double = 0.0
) {
    fun toDomain(): SpendingTrend {
        return SpendingTrend(
            date = parseDate(date),
            amount = amount
        )
    }
    
    private fun parseDate(dateStr: String): LocalDate {
        return try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)
        } catch (e: Exception) {
            try {
                LocalDate.parse(dateStr.substringBefore("T"))
            } catch (e2: Exception) {
                LocalDate.now()
            }
        }
    }
}

data class CategorySpendingDto(
    val categoryId: String? = null,
    val categoryName: String = "Other",
    val categoryIcon: String? = null,
    val categoryColor: String? = null,
    val amount: Double = 0.0,
    val percentage: Double = 0.0,
    val transactionCount: Int = 0
) {
    fun toDomain(): CategorySpending {
        val category = Category.entries.find {
            it.displayName.equals(categoryName, ignoreCase = true)
        } ?: Category.OTHER_EXPENSE
        
        return CategorySpending(
            category = category,
            amount = amount,
            percentage = percentage.toFloat(),
            transactionCount = transactionCount
        )
    }
}

data class AnalyticsResponseDto(
    val startDate: String = "",
    val endDate: String = "",
    val dateRangeLabel: String = "",
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netSavings: Double = 0.0,
    val categoryBreakdown: List<CategorySpendingDto>? = null,
    val dailySpending: List<SpendingTrendDto>? = null,
    val monthlyComparison: List<MonthlyComparisonDto>? = null,
    val topMerchants: List<MerchantSpendingDto>? = null
)

data class MonthlyComparisonDto(
    val year: Int = 2024,
    val month: Int = 1,
    val monthName: String = "",
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val savings: Double = 0.0
) {
    fun toDomain(): MonthlySpending {
        return MonthlySpending(
            month = YearMonth.of(year, month),
            income = income,
            expense = expense
        )
    }
}

data class MerchantSpendingDto(
    val merchantName: String = "",
    val categoryName: String? = null,
    val totalAmount: Double = 0.0,
    val transactionCount: Int = 0
) {
    fun toDomain(): MerchantSpending {
        val category = categoryName?.let {
            Category.entries.find { cat ->
                cat.displayName.equals(it, ignoreCase = true)
            }
        } ?: Category.OTHER_EXPENSE
        
        return MerchantSpending(
            merchantName = merchantName,
            totalAmount = totalAmount,
            transactionCount = transactionCount,
            category = category
        )
    }
}

data class DateRangeRequestDto(
    val rangeType: String = "THIS_MONTH",
    val customStartDate: String? = null,
    val customEndDate: String? = null
)
