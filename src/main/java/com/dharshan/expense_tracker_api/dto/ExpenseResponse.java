package com.dharshan.expense_tracker_api.dto;

import com.dharshan.expense_tracker_api.model.ExpenseStatus;
import lombok.Builder;
import lombok.Data;
import com.dharshan.expense_tracker_api.model.ExpenseSource;
import java.time.LocalDate;

@Data
@Builder
public class ExpenseResponse {

    private Long id;

    private String title;

    private Double amount;

    private String category;

    private String description;

    private LocalDate date;

    private ExpenseStatus status;

    private String merchantName;
    private ExpenseSource source;
    private String transactionId;
}