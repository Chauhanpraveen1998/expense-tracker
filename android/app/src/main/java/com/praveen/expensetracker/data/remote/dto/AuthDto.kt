package com.praveen.expensetracker.data.remote.dto

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
    @SerializedName("refreshToken")
    val refreshToken: String? = null,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("expiresIn")
    val expiresIn: Long? = null
)

data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    @SerializedName("monthlyBudget")
    val monthlyBudget: Double? = null,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null
)
