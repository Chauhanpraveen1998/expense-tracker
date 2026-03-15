package com.praveen.expensetracker.domain.usecase

import com.praveen.expensetracker.domain.model.AnalyticsData
import com.praveen.expensetracker.domain.model.DateRange
import com.praveen.expensetracker.domain.model.MerchantSpending
import com.praveen.expensetracker.domain.model.MonthlySpending
import com.praveen.expensetracker.domain.model.Transaction
import com.praveen.expensetracker.domain.model.TransactionType
import com.praveen.expensetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

class GetAnalyticsDataUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(dateRange: DateRange): AnalyticsData {
        val startDateTime = dateRange.startDate.atStartOfDay()
        val endDateTime = dateRange.endDate.atTime(LocalTime.MAX)
        
        val transactions = repository.getTransactionsByDateRange(startDateTime, endDateTime).first()
        
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        
        val totalExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        
        val netSavings = totalIncome - totalExpense
        
        val categoryBreakdown = repository.getCategorySpending(startDateTime, endDateTime)
        
        val dailySpending = repository.getDailySpendingTrends(
            dateRange.startDate,
            dateRange.endDate
        )
        
        val monthlyComparison = getMonthlyComparison()
        
        val topMerchants = getTopMerchants(transactions)
        
        return AnalyticsData(
            dateRange = dateRange,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            netSavings = netSavings,
            categoryBreakdown = categoryBreakdown,
            dailySpending = dailySpending,
            monthlyComparison = monthlyComparison,
            topMerchants = topMerchants
        )
    }
    
    private suspend fun getMonthlyComparison(): List<MonthlySpending> {
        val currentMonth = YearMonth.now()
        val months = (0..5).map { currentMonth.minusMonths(it.toLong()) }.reversed()
        
        return months.map { month ->
            val startDate = month.atDay(1).atStartOfDay()
            val endDate = month.atEndOfMonth().atTime(LocalTime.MAX)
            
            val income = repository.getTotalIncome(startDate, endDate)
            val expense = repository.getTotalExpense(startDate, endDate)
            
            MonthlySpending(
                month = month,
                income = income,
                expense = expense
            )
        }
    }
    
    private fun getTopMerchants(transactions: List<Transaction>): List<MerchantSpending> {
        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.merchantName }
            .map { (merchant, txns) ->
                MerchantSpending(
                    merchantName = merchant,
                    totalAmount = txns.sumOf { it.amount },
                    transactionCount = txns.size,
                    category = txns.first().category
                )
            }
            .sortedByDescending { it.totalAmount }
            .take(5)
    }
}
