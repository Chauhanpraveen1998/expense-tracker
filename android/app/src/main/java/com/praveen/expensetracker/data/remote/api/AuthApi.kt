package com.praveen.expensetracker.data.remote.api

import com.praveen.expensetracker.data.remote.dto.AuthResponse
import com.praveen.expensetracker.data.remote.dto.LoginRequest
import com.praveen.expensetracker.data.remote.dto.RegisterRequest
import com.praveen.expensetracker.data.remote.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserResponse>
    
    @POST("auth/refresh")
    suspend fun refreshToken(): Response<AuthResponse>
}
