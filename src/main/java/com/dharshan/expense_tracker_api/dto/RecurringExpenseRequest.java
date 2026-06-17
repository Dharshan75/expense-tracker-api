package com.dharshan.expense_tracker_api.dto;

import lombok.Data;

@Data
public class RecurringExpenseRequest {

    private String title;

    private String category;

    private Double amount;

    private String frequency;
}