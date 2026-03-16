package com.praveen.expensetracker.data.remote.api

import com.praveen.expensetracker.data.remote.dto.AccountRequest
import com.praveen.expensetracker.data.remote.dto.AccountResponse
import com.praveen.expensetracker.data.remote.dto.AccountSummaryResponse
import retrofit2.Response
import retrofit2.http.*

interface AccountApi {
    @GET("accounts")
    suspend fun getAllAccounts(): Response<List<AccountResponse>>
    
    @GET("accounts/summary")
    suspend fun getAccountSummary(): Response<AccountSummaryResponse>
    
    @GET("accounts/{id}")
    suspend fun getAccountById(@Path("id") id: String): Response<AccountResponse>
    
    @POST("accounts")
    suspend fun createAccount(@Body request: AccountRequest): Response<AccountResponse>
    
    @PUT("accounts/{id}")
    suspend fun updateAccount(
        @Path("id") id: String,
        @Body request: AccountRequest
    ): Response<AccountResponse>
    
    @DELETE("accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: String): Response<Unit>
    
    @PATCH("accounts/{id}/balance")
    suspend fun updateBalance(
        @Path("id") id: String,
        @Query("amount") amount: Double
    ): Response<AccountResponse>
}
