package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BudgetUsageResponse {

    private UUID id;

    private String category;

    private Double budgetAmount;

    private Double spentAmount;

    private Double remainingAmount;

    private Double percentageUsed;

    private Boolean exceeded;
}