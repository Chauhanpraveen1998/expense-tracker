package com.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private BigDecimal totalBudget;
    private BigDecimal totalSpent;
    private BigDecimal totalIncome;
    private BigDecimal safeToSpend;
    private Double budgetUsagePercentage;
    
    private List<InsightResponse> insights;
    private List<SpendingTrendResponse> spendingTrends;
    private List<ExpenseResponse> recentTransactions;
    private List<CategorySpendingResponse> topCategories;
    
    private AccountSummaryResponse accountSummary;
}
