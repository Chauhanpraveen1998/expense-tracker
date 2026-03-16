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
public class MerchantSpendingResponse {

    private String merchantName;
    private String categoryName;
    private BigDecimal totalAmount;
    private Integer transactionCount;
}
