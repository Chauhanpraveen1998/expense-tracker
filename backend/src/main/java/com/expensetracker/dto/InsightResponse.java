package com.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsightResponse {

    private InsightType type;
    private String title;
    private String description;
    private String actionText;

    public enum InsightType {
        TIP, WARNING, INFO, TREND
    }
}
