package com.dharshan.expense_tracker_api.dto;

import lombok.Data;

import java.time.LocalDate;
import com.dharshan.expense_tracker_api.model.ExpenseSource;
@Data
public class ExpenseRequest {

    private String title;

    private Double amount;

    private String category;

    private String description;

    private LocalDate date;
    private String merchantName;
    private ExpenseSource source;
    private String transactionId;
}