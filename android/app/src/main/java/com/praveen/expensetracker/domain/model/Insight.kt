package com.praveen.expensetracker.domain.model

data class Insight(
    val id: String,
    val title: String,
    val description: String,
    val type: InsightType,
    val actionRoute: String? = null,
    val relatedCategory: Category? = null,
    val percentageChange: Double? = null
)

enum class InsightType {
    TIP,
    WARNING,
    INFO,
    TREND
}
