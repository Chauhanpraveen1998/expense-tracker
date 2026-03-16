package com.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyComparisonResponse {

    private Integer year;
    private Integer month;
    private String monthName;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal savings;
}
