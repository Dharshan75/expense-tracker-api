package com.dharshan.expense_tracker_api.dto;

import lombok.Data;

@Data
public class BudgetRequest {

    private String category;

    private Double amount;

    private Integer month;

    private Integer year;
}