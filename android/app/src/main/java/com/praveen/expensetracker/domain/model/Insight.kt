package com.praveen.expensetracker.domain.model

data class Insight(
    val id: String,
    val type: InsightType,
    val title: String,
    val description: String,
    val actionText: String? = null,
    val relatedCategory: Category? = null
)

enum class InsightType {
    TIP, WARNING, INFO, TREND
}
