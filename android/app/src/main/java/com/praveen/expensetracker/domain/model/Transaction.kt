package com.praveen.expensetracker.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val merchantName: String,
    val category: Category,
    val type: TransactionType,
    val dateTime: LocalDateTime,
    val accountId: String? = null,
    val note: String? = null,
    val merchantLogoUrl: String? = null,
    val isRecurring: Boolean = false,
    val tags: List<String> = emptyList()
)

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class Category(val displayName: String, val icon: String) {
    FOOD_DINING("Food & Dining", "restaurant"),
    SHOPPING("Shopping", "shopping_bag"),
    TRANSPORT("Transport", "directions_car"),
    ENTERTAINMENT("Entertainment", "movie"),
    BILLS_UTILITIES("Bills & Utilities", "receipt"),
    HEALTH("Health", "medical_services"),
    EDUCATION("Education", "school"),
    TRAVEL("Travel", "flight"),
    GROCERIES("Groceries", "local_grocery_store"),
    FUEL("Fuel", "local_gas_station"),
    PERSONAL_CARE("Personal Care", "spa"),
    GIFTS("Gifts & Donations", "card_giftcard"),
    INVESTMENTS("Investments", "trending_up"),
    SALARY("Salary", "payments"),
    FREELANCE("Freelance", "work"),
    REFUND("Refund", "replay"),
    OTHER_INCOME("Other Income", "attach_money"),
    OTHER_EXPENSE("Other", "category"),
    TRANSFER("Transfer", "swap_horiz")
}
