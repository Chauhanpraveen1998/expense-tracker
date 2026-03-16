package com.praveen.expensetracker.data.remote.dto

data class SmsTransactionRequest(
    val amount: Double,
    val transactionType: String,
    val merchantName: String,
    val categoryName: String? = null,
    val accountId: String? = null,
    val accountLastFour: String? = null,
    val dateTime: String? = null,
    val referenceNumber: String? = null,
    val smsHash: String,
    val rawMessage: String? = null
)

data class SmsTransactionResponse(
    val success: Boolean,
    val isDuplicate: Boolean,
    val message: String,
    val transaction: TransactionResponse? = null
)
