package com.expensetracker.dto;

import com.expensetracker.entity.Account.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must be less than 100 characters")
    private String name;

    @NotNull(message = "Account type is required")
    private AccountType type;

    private BigDecimal balance = BigDecimal.ZERO;

    @Size(max = 100, message = "Bank name must be less than 100 characters")
    private String bankName;

    @Size(max = 4, message = "Last four digits must be exactly 4 characters")
    private String lastFourDigits;

    private String colorPrimary = "#1E3A8A";

    private String colorSecondary = "#3B82F6";
}
