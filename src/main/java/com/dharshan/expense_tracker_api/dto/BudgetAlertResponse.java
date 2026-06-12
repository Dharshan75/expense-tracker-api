package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetAlertResponse {

    private String category;

    private Double percentageUsed;

    private String message;

    private Boolean exceeded;
}