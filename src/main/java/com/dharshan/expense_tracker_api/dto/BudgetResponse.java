package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BudgetResponse {

    private UUID id;
    private String category;
    private Double amount;
    private Integer month;
    private Integer year;
}