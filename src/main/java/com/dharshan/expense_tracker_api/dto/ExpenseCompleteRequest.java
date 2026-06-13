package com.dharshan.expense_tracker_api.dto;

import lombok.Data;

@Data
public class ExpenseCompleteRequest {

    private String title;

    private String category;
}