package com.praveen.expensetracker.data.repository

import com.praveen.expensetracker.data.api.AuthApi
import com.praveen.expensetracker.data.api.BudgetApi
import com.praveen.expensetracker.data.api.CategoryApi
import com.praveen.expensetracker.data.api.ExpenseApi
import com.praveen.expensetracker.data.model.*
import com.praveen.expensetracker.util.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
}

object NetworkErrorCodes {
    const val UNAUTHORIZED = 401
    const val FORBIDDEN = 403
    const val NOT_FOUND = 404
    const val SERVER_ERROR = 500
    const val NETWORK_ERROR = -1
}

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveToken(authResponse.token)
                Result.Success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = parseErrorMessage(errorBody) ?: "Invalid email or password"
                Result.Error(message, response.code())
            }
        } catch (e: Exception) {
            val message = getErrorMessage(e)
            Result.Error(message, NetworkErrorCodes.NETWORK_ERROR)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.register(RegisterRequest(name, email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveToken(authResponse.token)
                Result.Success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = parseErrorMessage(errorBody) ?: "Registration failed"
                Result.Error(message, response.code())
            }
        } catch (e: Exception) {
            val message = getErrorMessage(e)
            Result.Error(message, NetworkErrorCodes.NETWORK_ERROR)
        }
    }

    suspend fun logout() {
        tokenManager.clearToken()
    }

    fun isLoggedIn(): Flow<Boolean> = tokenManager.token.map { it != null }

    private fun parseErrorMessage(errorBody: String?): String? {
        if (errorBody == null) return null
        return try {
            val json = com.google.gson.Gson().fromJson(errorBody, Map::class.java)
            (json["message"] as? String) ?: (json["error"] as? String)
        } catch (e: Exception) {
            null
        }
    }

    private fun getErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("Unable to resolve host") == true -> "Check your internet connection"
            e.message?.contains("timeout") == true -> "Connection timed out"
            else -> "Network error: ${e.message}"
        }
    }
}

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseApi: ExpenseApi
) {
    suspend fun getExpenses(page: Int = 0, size: Int = 20, categoryId: String? = null): Result<List<Expense>> {
        return try {
            val response = if (categoryId != null) {
                expenseApi.getExpensesByCategory(categoryId)
            } else {
                expenseApi.getExpenses(page, size)
            }
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                handleError(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun getExpensesByCategory(categoryId: String): Result<List<Expense>> {
        return try {
            val response = expenseApi.getExpensesByCategory(categoryId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                handleError(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun addExpense(amount: Double, description: String, categoryId: String, date: String): Result<Expense> {
        return try {
            // Backend expects expenseDate instead of date
            val request = ExpenseRequest(amount, description, categoryId, date)
            val response = expenseApi.addExpense(request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                handleError(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun deleteExpense(id: String): Result<Unit> {
        return try {
            val response = expenseApi.deleteExpense(id)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                handleError(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun getRecentExpenses(limit: Int = 5): Result<List<Expense>> {
        return try {
            val response = expenseApi.getRecentExpenses(limit)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                handleError(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun getMonthlyTotal(): Result<Double> {
        return try {
            val response = expenseApi.getMonthlyTotal()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                handleError(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun getExpenseSummary(): Result<ExpenseSummary> {
        return try {
            val response = expenseApi.getExpenseSummary()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                handleError(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun getDailyExpenses(days: Int = 7): Result<List<DailyExpense>> {
        return try {
            val response = expenseApi.getDailyExpenses(days)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                handleError(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun getDashboardData(): Result<DashboardData> {
        return try {
            val totalResponse = expenseApi.getMonthlyTotal()
            val recentResponse = expenseApi.getRecentExpenses(5)
            
            if (totalResponse.isSuccessful && recentResponse.isSuccessful) {
                Result.Success(
                    DashboardData(
                        totalSpentThisMonth = totalResponse.body() ?: 0.0,
                        recentExpenses = recentResponse.body() ?: emptyList()
                    )
                )
            } else {
                val code = maxOf(totalResponse.code(), recentResponse.code())
                handleError(code, null)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun handleError(code: Int, errorBody: String?): Result.Error {
        return when (code) {
            NetworkErrorCodes.UNAUTHORIZED -> Result.Error("Session expired. Please login again.", code)
            NetworkErrorCodes.FORBIDDEN -> Result.Error("You don't have permission", code)
            NetworkErrorCodes.NOT_FOUND -> Result.Error("Resource not found", code)
            400 -> {
                val message = parseErrorMessage(errorBody) ?: "Invalid data. Please check your input."
                Result.Error(message, code)
            }
            in 500..599 -> Result.Error("Server error. Please try again later.", code)
            else -> {
                val message = parseErrorMessage(errorBody) ?: "Request failed with code $code"
                Result.Error(message, code)
            }
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val json = com.google.gson.Gson().fromJson(errorBody, Map::class.java)
            (json["message"] as? String) 
                ?: (json["error"] as? String)
                ?: (json["reason"] as? String)
                ?: (json["title"] as? String)
        } catch (e: Exception) {
            errorBody.take(100)
        }
    }

    private fun handleException(e: Exception): Result.Error {
        val message = when {
            e.message?.contains("Unable to resolve host") == true -> "Cannot connect to server. Check your internet connection."
            e.message?.contains("timeout") == true -> "Connection timed out. Please try again."
            e.message?.contains("connection") == true -> "Cannot connect to server. Is the backend running on port 8080?"
            e.message?.contains("401") == true -> "Session expired. Please login again."
            e.message?.contains("403") == true -> "Not authorized. Please login again."
            else -> "Error: ${e.message ?: "Unknown error"}"
        }
        return Result.Error(message, NetworkErrorCodes.NETWORK_ERROR)
    }
}

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryApi: CategoryApi
) {
    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = categoryApi.getCategories()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to fetch categories")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetApi: BudgetApi
) {
    suspend fun getBudgets(): Result<List<Budget>> {
        return try {
            val response = budgetApi.getBudgets()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to fetch budgets")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun createBudget(category: String, amount: Double, period: String): Result<Budget> {
        return try {
            val response = budgetApi.createBudget(BudgetRequest(category, amount, period))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to create budget")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getBudgetStatus(): Result<List<BudgetStatus>> {
        return try {
            val response = budgetApi.getBudgetStatus()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to fetch budget status")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
