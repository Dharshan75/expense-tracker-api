package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyTrendResponse {

    private String month;

    private Double amount;
}