package com.expensetracker.dto;

import java.util.List;

public record SummaryResponse(
        double totalThisMonth,
        double totalLastMonth,
        List<CategorySummary> byCategory
) {}
