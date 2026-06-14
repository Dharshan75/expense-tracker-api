package com.dharshan.expense_tracker_api.dto;

import com.dharshan.expense_tracker_api.model.ExpenseSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseRequest {

    private String title;

    @NotNull(message = "Amount is required")
    private Double amount;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    private LocalDate date;

    private String merchantName;

    private ExpenseSource source;

    private String transactionId;
}