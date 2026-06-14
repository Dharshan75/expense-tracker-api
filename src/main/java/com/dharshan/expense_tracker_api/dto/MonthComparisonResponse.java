package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthComparisonResponse {

    private Double currentMonthExpense;

    private Double previousMonthExpense;

    private Double percentageChange;

    private String trend;
}
