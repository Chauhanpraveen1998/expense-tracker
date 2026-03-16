package com.praveen.expensetracker.data.remote.api

import com.praveen.expensetracker.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AnalyticsApi {
    @GET("analytics/dashboard")
    suspend fun getDashboard(): Response<DashboardResponseDto>
    
    @POST("analytics")
    suspend fun getAnalytics(@Body request: DateRangeRequestDto): Response<AnalyticsResponseDto>
    
    @GET("analytics/category-spending")
    suspend fun getCategorySpending(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<CategorySpendingDto>>
    
    @GET("analytics/spending-trends")
    suspend fun getSpendingTrends(
        @Query("days") days: Int = 7
    ): Response<List<SpendingTrendDto>>
    
    @GET("analytics/monthly-comparison")
    suspend fun getMonthlyComparison(
        @Query("months") months: Int = 6
    ): Response<List<MonthlyComparisonDto>>
    
    @GET("analytics/top-merchants")
    suspend fun getTopMerchants(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("limit") limit: Int = 5
    ): Response<List<MerchantSpendingDto>>
}
