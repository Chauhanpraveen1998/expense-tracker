package com.expensetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ExpenseRequest {

    @NotNull @DecimalMin("0.01")
    private BigDecimal amount;

    private String description;

    @NotNull
    private LocalDate expenseDate;

    private UUID categoryId;
}
