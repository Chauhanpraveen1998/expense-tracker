package com.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeRequest {

    private DateRangeType rangeType;
    private LocalDate customStartDate;
    private LocalDate customEndDate;

    public enum DateRangeType {
        THIS_WEEK,
        THIS_MONTH,
        LAST_MONTH,
        LAST_3_MONTHS,
        THIS_YEAR,
        CUSTOM
    }
}
