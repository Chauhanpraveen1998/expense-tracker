package com.expensetracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record BudgetStatusResponse(
        @JsonProperty("id")         UUID budgetId,
        @JsonProperty("category")   String categoryName,
                                    double budgetAmount,
        @JsonProperty("spentAmount") double amountSpent,
                                    double percentageUsed,
                                    boolean isOverBudget
) {}
