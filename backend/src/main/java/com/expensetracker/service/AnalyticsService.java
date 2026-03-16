package com.expensetracker.service;

import com.expensetracker.dto.*;
import com.expensetracker.dto.DateRangeRequest.DateRangeType;
import com.expensetracker.dto.InsightResponse.InsightType;
import com.expensetracker.entity.Expense.TransactionType;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.AccountRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ExpenseRepository expenseRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ExpenseService expenseService;

    private static final BigDecimal DEFAULT_BUDGET = BigDecimal.valueOf(50000);

    public DashboardResponse getDashboard(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        BigDecimal totalIncome = expenseRepository.sumByTypeAndDateRange(
                userId, TransactionType.INCOME, startOfMonth, endOfMonth);
        BigDecimal totalExpense = expenseRepository.sumByTypeAndDateRange(
                userId, TransactionType.EXPENSE, startOfMonth, endOfMonth);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal totalBudget = user.getMonthlyBudget() != null 
                ? BigDecimal.valueOf(user.getMonthlyBudget()) 
                : DEFAULT_BUDGET;

        BigDecimal safeToSpend = totalBudget.subtract(totalExpense).max(BigDecimal.ZERO);

        Double budgetUsagePercentage = totalBudget.compareTo(BigDecimal.ZERO) > 0
                ? totalExpense.divide(totalBudget, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        List<InsightResponse> insights = generateInsights(userId, totalIncome, totalExpense, totalBudget, startOfMonth, endOfMonth);
        List<SpendingTrendResponse> spendingTrends = getSpendingTrends(userId, 7);
        List<ExpenseResponse> recentTransactions = expenseService.getRecentExpenses(userId);
        List<CategorySpendingResponse> topCategories = getCategorySpending(userId, startOfMonth, endOfMonth);
        AccountSummaryResponse accountSummary = getAccountSummary(userId);

        return DashboardResponse.builder()
                .totalBudget(totalBudget)
                .totalSpent(totalExpense)
                .totalIncome(totalIncome)
                .safeToSpend(safeToSpend)
                .budgetUsagePercentage(budgetUsagePercentage)
                .insights(insights)
                .spendingTrends(spendingTrends)
                .recentTransactions(recentTransactions)
                .topCategories(topCategories)
                .accountSummary(accountSummary)
                .build();
    }

    public AnalyticsResponse getAnalytics(UUID userId, DateRangeRequest request) {
        LocalDate[] dateRange = resolveDateRange(request);
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        BigDecimal totalIncome = expenseRepository.sumByTypeAndDateRange(
                userId, TransactionType.INCOME, startDateTime, endDateTime);
        BigDecimal totalExpense = expenseRepository.sumByTypeAndDateRange(
                userId, TransactionType.EXPENSE, startDateTime, endDateTime);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        List<CategorySpendingResponse> categoryBreakdown = getCategorySpending(userId, startDateTime, endDateTime);
        List<SpendingTrendResponse> dailySpending = getDailySpending(userId, startDateTime, endDateTime);
        List<MonthlyComparisonResponse> monthlyComparison = getMonthlyComparison(userId, 6);
        List<MerchantSpendingResponse> topMerchants = getTopMerchants(userId, startDateTime, endDateTime, 5);

        return AnalyticsResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .dateRangeLabel(formatDateRangeLabel(request.getRangeType(), startDate, endDate))
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(netSavings)
                .categoryBreakdown(categoryBreakdown)
                .dailySpending(dailySpending)
                .monthlyComparison(monthlyComparison)
                .topMerchants(topMerchants)
                .build();
    }

    public List<CategorySpendingResponse> getCategorySpending(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = expenseRepository.getCategorySpending(userId, startDate, endDate);

        BigDecimal totalExpense = results.stream()
                .map(r -> (BigDecimal) r[2])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return results.stream()
                .map(row -> {
                    UUID categoryId = (UUID) row[0];
                    String categoryName = (String) row[1];
                    BigDecimal amount = (BigDecimal) row[2];
                    Long count = (Long) row[3];

                    Double percentage = totalExpense.compareTo(BigDecimal.ZERO) > 0
                            ? amount.divide(totalExpense, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;

                    return CategorySpendingResponse.builder()
                            .categoryId(categoryId)
                            .categoryName(categoryName)
                            .amount(amount)
                            .percentage(percentage)
                            .transactionCount(count.intValue())
                            .build();
                })
                .toList();
    }

    public List<SpendingTrendResponse> getSpendingTrends(UUID userId, int days) {
        LocalDate today = LocalDate.now();
        LocalDateTime startDate = today.minusDays(days - 1).atStartOfDay();
        LocalDateTime endDate = today.atTime(LocalTime.MAX);

        return getDailySpending(userId, startDate, endDate);
    }

    private List<SpendingTrendResponse> getDailySpending(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = expenseRepository.getDailySpending(userId, startDate, endDate);

        Map<LocalDate, BigDecimal> spendingMap = results.stream()
                .collect(java.util.stream.Collectors.toMap(
                        r -> (LocalDate) r[0],
                        r -> (BigDecimal) r[1]
                ));

        List<SpendingTrendResponse> trends = new ArrayList<>();
        LocalDate current = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();

        while (!current.isAfter(end)) {
            trends.add(SpendingTrendResponse.builder()
                    .date(current)
                    .amount(spendingMap.getOrDefault(current, BigDecimal.ZERO))
                    .build());
            current = current.plusDays(1);
        }

        return trends;
    }

    public List<MonthlyComparisonResponse> getMonthlyComparison(UUID userId, int months) {
        List<MonthlyComparisonResponse> comparisons = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            LocalDateTime startDate = month.atDay(1).atStartOfDay();
            LocalDateTime endDate = month.atEndOfMonth().atTime(LocalTime.MAX);

            BigDecimal income = expenseRepository.sumByTypeAndDateRange(
                    userId, TransactionType.INCOME, startDate, endDate);
            BigDecimal expense = expenseRepository.sumByTypeAndDateRange(
                    userId, TransactionType.EXPENSE, startDate, endDate);

            if (income == null) income = BigDecimal.ZERO;
            if (expense == null) expense = BigDecimal.ZERO;

            comparisons.add(MonthlyComparisonResponse.builder()
                    .year(month.getYear())
                    .month(month.getMonthValue())
                    .monthName(month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .income(income)
                    .expense(expense)
                    .savings(income.subtract(expense))
                    .build());
        }

        return comparisons;
    }

    public List<MerchantSpendingResponse> getTopMerchants(UUID userId, LocalDateTime startDate, LocalDateTime endDate, int limit) {
        List<Object[]> results = expenseRepository.getTopMerchants(userId, startDate, endDate, PageRequest.of(0, limit));

        return results.stream()
                .map(row -> MerchantSpendingResponse.builder()
                        .merchantName((String) row[0])
                        .totalAmount((BigDecimal) row[1])
                        .transactionCount(((Long) row[2]).intValue())
                        .build())
                .toList();
    }

    private List<InsightResponse> generateInsights(UUID userId, BigDecimal totalIncome, BigDecimal totalExpense, 
                                                    BigDecimal budget, LocalDateTime startDate, LocalDateTime endDate) {
        List<InsightResponse> insights = new ArrayList<>();

        double budgetUsage = budget.compareTo(BigDecimal.ZERO) > 0
                ? totalExpense.divide(budget, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        if (budgetUsage >= 90) {
            insights.add(InsightResponse.builder()
                    .type(InsightType.WARNING)
                    .title("Budget Alert")
                    .description(String.format("You've used %.0f%% of your monthly budget. Consider limiting expenses.", budgetUsage))
                    .actionText("View Details")
                    .build());
        } else if (budgetUsage >= 70) {
            insights.add(InsightResponse.builder()
                    .type(InsightType.INFO)
                    .title("Budget Update")
                    .description(String.format("You've used %.0f%% of your monthly budget. You're on track!", budgetUsage))
                    .actionText("View Budget")
                    .build());
        } else if (budgetUsage < 50 && totalExpense.compareTo(BigDecimal.ZERO) > 0) {
            insights.add(InsightResponse.builder()
                    .type(InsightType.TIP)
                    .title("Great Savings!")
                    .description(String.format("You've only used %.0f%% of your budget. Keep up the good work!", budgetUsage))
                    .actionText("View Savings")
                    .build());
        }

        List<CategorySpendingResponse> categories = getCategorySpending(userId, startDate, endDate);
        if (!categories.isEmpty()) {
            CategorySpendingResponse topCategory = categories.get(0);
            if (topCategory.getPercentage() >= 30) {
                insights.add(InsightResponse.builder()
                        .type(InsightType.TREND)
                        .title("Top Spending: " + topCategory.getCategoryName())
                        .description(String.format("%s accounts for %.0f%% of your expenses this month.",
                                topCategory.getCategoryName(), topCategory.getPercentage()))
                        .actionText("Analyze")
                        .build());
            }
        }

        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            double savingsRate = totalIncome.subtract(totalExpense)
                    .divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();

            if (savingsRate >= 20) {
                insights.add(InsightResponse.builder()
                        .type(InsightType.TIP)
                        .title("Healthy Savings")
                        .description(String.format("You're saving %.0f%% of your income. Excellent financial health!", savingsRate))
                        .actionText("Investment Tips")
                        .build());
            } else if (savingsRate < 0) {
                insights.add(InsightResponse.builder()
                        .type(InsightType.WARNING)
                        .title("Overspending Alert")
                        .description(String.format("Your expenses exceed your income by ₹%,.0f",
                                totalExpense.subtract(totalIncome).doubleValue()))
                        .actionText("Review Expenses")
                        .build());
            }
        }

        if (insights.isEmpty()) {
            insights.add(InsightResponse.builder()
                    .type(InsightType.TIP)
                    .title("Start Tracking")
                    .description("Add your transactions to get personalized insights and manage your finances better.")
                    .actionText("Add Transaction")
                    .build());
        }

        return insights.stream().limit(4).toList();
    }

    private AccountSummaryResponse getAccountSummary(UUID userId) {
        BigDecimal totalBalance = accountRepository.getTotalBalanceByUserId(userId);
        Long accountCount = accountRepository.countActiveByUserId(userId);

        return AccountSummaryResponse.builder()
                .totalBalance(totalBalance != null ? totalBalance : BigDecimal.ZERO)
                .accountCount(accountCount != null ? accountCount : 0L)
                .build();
    }

    private LocalDate[] resolveDateRange(DateRangeRequest request) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = today;

        if (request == null || request.getRangeType() == null) {
            startDate = today.withDayOfMonth(1);
        } else {
            switch (request.getRangeType()) {
                case THIS_WEEK:
                    startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    break;
                case THIS_MONTH:
                    startDate = today.withDayOfMonth(1);
                    break;
                case LAST_MONTH:
                    YearMonth lastMonth = YearMonth.now().minusMonths(1);
                    startDate = lastMonth.atDay(1);
                    endDate = lastMonth.atEndOfMonth();
                    break;
                case LAST_3_MONTHS:
                    startDate = today.minusMonths(3);
                    break;
                case THIS_YEAR:
                    startDate = today.withDayOfYear(1);
                    break;
                case CUSTOM:
                    startDate = request.getCustomStartDate() != null ? request.getCustomStartDate() : today.minusMonths(1);
                    endDate = request.getCustomEndDate() != null ? request.getCustomEndDate() : today;
                    break;
                default:
                    startDate = today.withDayOfMonth(1);
            }
        }

        return new LocalDate[]{startDate, endDate};
    }

    private String formatDateRangeLabel(DateRangeType type, LocalDate start, LocalDate end) {
        if (type == null) return "This Month";

        return switch (type) {
            case THIS_WEEK -> "This Week";
            case THIS_MONTH -> "This Month";
            case LAST_MONTH -> "Last Month";
            case LAST_3_MONTHS -> "Last 3 Months";
            case THIS_YEAR -> "This Year";
            case CUSTOM -> start + " - " + end;
        };
    }
}
