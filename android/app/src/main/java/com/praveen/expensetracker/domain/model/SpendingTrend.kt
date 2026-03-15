package com.praveen.expensetracker.domain.model

import java.time.LocalDate

data class SpendingTrend(
    val date: LocalDate,
    val amount: Double
)

data class SpendingTrendData(
    val trends: List<SpendingTrend>,
    val totalSpent: Double,
    val averageDaily: Double,
    val highestDay: SpendingTrend?,
    val lowestDay: SpendingTrend?
)
