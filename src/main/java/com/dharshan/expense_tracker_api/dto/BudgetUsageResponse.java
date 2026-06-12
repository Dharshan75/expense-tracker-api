package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetUsageResponse {

    private String category;

    private Double budgetAmount;

    private Double spentAmount;

    private Double remainingAmount;

    private Double percentageUsed;

    private Boolean exceeded;
}