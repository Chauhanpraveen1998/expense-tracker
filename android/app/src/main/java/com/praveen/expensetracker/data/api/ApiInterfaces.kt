package com.praveen.expensetracker.data.api

import com.praveen.expensetracker.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}

interface ExpenseApi {
    @GET("api/expenses")
    suspend fun getExpenses(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("categoryId") categoryId: String? = null
    ): Response<List<Expense>>

    @GET("api/expenses")
    suspend fun getExpensesByCategory(@Query("categoryId") categoryId: String): Response<List<Expense>>

    @POST("api/expenses")
    suspend fun addExpense(@Body request: ExpenseRequest): Response<Expense>

    @DELETE("api/expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: String): Response<Unit>

    @GET("api/expenses/recent")
    suspend fun getRecentExpenses(@Query("limit") limit: Int = 5): Response<List<Expense>>

    @GET("api/expenses/monthly-total")
    suspend fun getMonthlyTotal(): Response<Double>

    @GET("api/expenses/summary")
    suspend fun getExpenseSummary(): Response<ExpenseSummary>

    @GET("api/expenses/daily")
    suspend fun getDailyExpenses(@Query("days") days: Int = 7): Response<List<DailyExpense>>
}

interface CategoryApi {
    @GET("api/categories")
    suspend fun getCategories(): Response<List<Category>>
}

interface BudgetApi {
    @GET("api/budgets")
    suspend fun getBudgets(): Response<List<Budget>>

    @POST("api/budgets")
    suspend fun createBudget(@Body request: BudgetRequest): Response<Budget>

    @GET("api/budgets/status")
    suspend fun getBudgetStatus(): Response<List<BudgetStatus>>
}
