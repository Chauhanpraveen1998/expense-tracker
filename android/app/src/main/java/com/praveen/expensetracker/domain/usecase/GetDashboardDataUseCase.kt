package com.praveen.expensetracker.domain.usecase

import com.praveen.expensetracker.domain.model.CategorySpending
import com.praveen.expensetracker.domain.model.DashboardData
import com.praveen.expensetracker.domain.model.Insight
import com.praveen.expensetracker.domain.model.InsightType
import com.praveen.expensetracker.domain.model.SpendingTrend
import com.praveen.expensetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class GetDashboardDataUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        monthlyBudget: Double = 50000.0
    ): DashboardData {
        val now = LocalDateTime.now()
        val startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay()
        val endOfMonth = now.toLocalDate()
            .withDayOfMonth(now.toLocalDate().lengthOfMonth())
            .atTime(LocalTime.MAX)
        
        val totalIncome = repository.getTotalIncome(startOfMonth, endOfMonth)
        val totalExpense = repository.getTotalExpense(startOfMonth, endOfMonth)
        val safeToSpend = (monthlyBudget - totalExpense).coerceAtLeast(0.0)
        val budgetUsedPercentage = if (monthlyBudget > 0) {
            (totalExpense / monthlyBudget * 100).toFloat()
        } else 0f
        
        val recentTransactions = repository.getRecentTransactions(10).first()
        
        val categorySpending = repository.getCategorySpending(startOfMonth, endOfMonth)
        
        val trendStartDate = LocalDate.now().minusDays(6)
        val trendEndDate = LocalDate.now()
        val spendingTrends = repository.getDailySpendingTrends(trendStartDate, trendEndDate)
        
        val insights = generateInsights(
            totalExpense = totalExpense,
            budget = monthlyBudget,
            categorySpending = categorySpending
        )
        
        return DashboardData(
            totalBudget = monthlyBudget,
            totalSpent = totalExpense,
            totalIncome = totalIncome,
            safeToSpend = safeToSpend,
            budgetUsedPercentage = budgetUsedPercentage,
            insights = insights,
            spendingTrends = spendingTrends,
            recentTransactions = recentTransactions,
            topCategories = categorySpending.take(5)
        )
    }
    
    private fun generateInsights(
        totalExpense: Double,
        budget: Double,
        categorySpending: List<CategorySpending>
    ): List<Insight> {
        val insights = mutableListOf<Insight>()
        
        val budgetPercentage = if (budget > 0) totalExpense / budget * 100 else 0.0
        when {
            budgetPercentage > 100 -> {
                insights.add(
                    Insight(
                        id = "budget_exceeded",
                        title = "Budget Exceeded!",
                        description = "You've spent ${String.format("%.0f", budgetPercentage - 100)}% more than your budget",
                        type = InsightType.WARNING
                    )
                )
            }
            budgetPercentage > 80 -> {
                insights.add(
                    Insight(
                        id = "budget_warning",
                        title = "Approaching Budget Limit",
                        description = "You've used ${String.format("%.0f", budgetPercentage)}% of your monthly budget",
                        type = InsightType.WARNING
                    )
                )
            }
            budgetPercentage < 50 -> {
                insights.add(
                    Insight(
                        id = "budget_good",
                        title = "Budget on Track!",
                        description = "You're doing great! ${String.format("%.0f", 100 - budgetPercentage)}% of budget remaining",
                        type = InsightType.TIP
                    )
                )
            }
        }
        
        categorySpending.firstOrNull()?.let { topCategory ->
            insights.add(
                Insight(
                    id = "top_category",
                    title = "${topCategory.category.displayName} is your top spend",
                    description = "₹${String.format("%.0f", topCategory.amount)} spent (${String.format("%.0f", topCategory.percentage)}% of total)",
                    type = InsightType.INFO,
                    relatedCategory = topCategory.category
                )
            )
        }
        
        return insights
    }
}
