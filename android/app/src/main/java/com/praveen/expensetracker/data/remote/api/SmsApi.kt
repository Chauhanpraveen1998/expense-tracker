package com.praveen.expensetracker.data.remote.api

import com.praveen.expensetracker.data.remote.dto.SmsTransactionRequest
import com.praveen.expensetracker.data.remote.dto.SmsTransactionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SmsApi {
    @POST("sms/sync")
    suspend fun syncSmsTransaction(@Body request: SmsTransactionRequest): Response<SmsTransactionResponse>
    
    @POST("sms/sync/batch")
    suspend fun syncBatchSmsTransactions(@Body requests: List<SmsTransactionRequest>): Response<List<SmsTransactionResponse>>
}
