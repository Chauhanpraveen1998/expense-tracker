package com.praveen.expensetracker.data.remote.api

import com.praveen.expensetracker.data.remote.dto.PagedResponse
import com.praveen.expensetracker.data.remote.dto.TransactionRequest
import com.praveen.expensetracker.data.remote.dto.TransactionResponse
import retrofit2.Response
import retrofit2.http.*

interface TransactionApi {
    @GET("expenses")
    suspend fun getTransactions(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("type") type: String? = null,
        @Query("categoryId") categoryId: String? = null,
        @Query("accountId") accountId: String? = null
    ): Response<PagedResponse<TransactionResponse>>
    
    @GET("expenses/search")
    suspend fun searchTransactions(
        @Query("query") query: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PagedResponse<TransactionResponse>>
    
    @GET("expenses/date-range")
    suspend fun getTransactionsByDateRange(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<List<TransactionResponse>>
    
    @GET("expenses/recent")
    suspend fun getRecentTransactions(): Response<List<TransactionResponse>>
    
    @GET("expenses/{id}")
    suspend fun getTransactionById(@Path("id") id: String): Response<TransactionResponse>
    
    @POST("expenses")
    suspend fun createTransaction(@Body request: TransactionRequest): Response<TransactionResponse>
    
    @PUT("expenses/{id}")
    suspend fun updateTransaction(
        @Path("id") id: String,
        @Body request: TransactionRequest
    ): Response<TransactionResponse>
    
    @DELETE("expenses/{id}")
    suspend fun deleteTransaction(@Path("id") id: String): Response<Unit>
}
