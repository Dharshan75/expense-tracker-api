package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpendingPatternResponse {

    private String highestCategory;

    private String highestMerchant;

    private String highestPurpose;

    private Double averageDailyExpense;

    private Double totalExpense;
}