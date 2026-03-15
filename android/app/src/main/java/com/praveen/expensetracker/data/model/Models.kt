package com.praveen.expensetracker.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val email: String?,
    val fullName: String?,
    val user: User?
)

data class User(
    val id: String,
    val name: String?,
    val email: String
)

/** Category as returned nested inside an Expense by the backend. */
data class CategoryRef(
    val id: String,
    val name: String?,
    val color: String?,
    val icon: String?
)

data class Expense(
    val id: String,
    val amount: Double,
    val description: String,
    val category: CategoryRef?,
    @SerializedName("expenseDate") val expenseDate: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class ExpenseRequest(
    val amount: Double,
    val description: String,
    val categoryId: String,
    val expenseDate: String
)

data class Category(
    val id: String,
    val name: String
)

data class DashboardData(
    val totalSpentThisMonth: Double,
    val recentExpenses: List<Expense>
)

data class Budget(
    val id: String,
    val category: String,
    val amount: Double,
    val period: String,
    val userId: String
)

data class BudgetRequest(
    val category: String,
    val amount: Double,
    val period: String
)

data class BudgetStatus(
    val id: String,
    val category: String,
    val budgetAmount: Double,
    val spentAmount: Double,
    @SerializedName("isOverBudget") val isOverBudget: Boolean
)

data class ExpenseSummary(
    val totalAmount: Double,
    val transactionCount: Int,
    val averageAmount: Double
)

data class DailyExpense(
    val date: String,
    val amount: Double
)
