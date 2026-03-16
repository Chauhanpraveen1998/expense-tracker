package com.expensetracker.dto;

import com.expensetracker.entity.Account;
import com.expensetracker.entity.Account.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private UUID id;
    private String name;
    private AccountType type;
    private String typeDisplayName;
    private BigDecimal balance;
    private String bankName;
    private String lastFourDigits;
    private String colorPrimary;
    private String colorSecondary;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AccountResponse fromEntity(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .typeDisplayName(account.getType().getDisplayName())
                .balance(account.getBalance())
                .bankName(account.getBankName())
                .lastFourDigits(account.getLastFourDigits())
                .colorPrimary(account.getColorPrimary())
                .colorSecondary(account.getColorSecondary())
                .isActive(account.getIsActive())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
