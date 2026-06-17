package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RecurringExpenseResponse {

    private UUID id;

    private String title;

    private String category;

    private Double amount;

    private String frequency;
}