package com.expensetracker.controller;

import com.expensetracker.dto.*;
import com.expensetracker.entity.User;
import com.expensetracker.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(analyticsService.getDashboard(user.getId()));
    }

    @PostMapping
    public ResponseEntity<AnalyticsResponse> getAnalytics(
            @AuthenticationPrincipal User user,
            @RequestBody(required = false) DateRangeRequest request) {
        return ResponseEntity.ok(analyticsService.getAnalytics(user.getId(), request));
    }

    @GetMapping("/category-spending")
    public ResponseEntity<List<CategorySpendingResponse>> getCategorySpending(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start = startDate != null 
                ? LocalDateTime.parse(startDate + "T00:00:00")
                : LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        LocalDateTime end = endDate != null
                ? LocalDateTime.parse(endDate + "T23:59:59")
                : LocalDateTime.now();

        return ResponseEntity.ok(analyticsService.getCategorySpending(user.getId(), start, end));
    }

    @GetMapping("/spending-trends")
    public ResponseEntity<List<SpendingTrendResponse>> getSpendingTrends(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(analyticsService.getSpendingTrends(user.getId(), days));
    }

    @GetMapping("/monthly-comparison")
    public ResponseEntity<List<MonthlyComparisonResponse>> getMonthlyComparison(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(analyticsService.getMonthlyComparison(user.getId(), months));
    }

    @GetMapping("/top-merchants")
    public ResponseEntity<List<MerchantSpendingResponse>> getTopMerchants(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "5") int limit) {

        LocalDateTime start = startDate != null
                ? LocalDateTime.parse(startDate + "T00:00:00")
                : LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        LocalDateTime end = endDate != null
                ? LocalDateTime.parse(endDate + "T23:59:59")
                : LocalDateTime.now();

        return ResponseEntity.ok(analyticsService.getTopMerchants(user.getId(), start, end, limit));
    }
}
