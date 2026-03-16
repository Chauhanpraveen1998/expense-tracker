package com.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private String dateRangeLabel;
    
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;
    
    private List<CategorySpendingResponse> categoryBreakdown;
    private List<SpendingTrendResponse> dailySpending;
    private List<MonthlyComparisonResponse> monthlyComparison;
    private List<MerchantSpendingResponse> topMerchants;
}
