package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PurposeDetailsResponse {

    private String purpose;

    private Double totalAmount;

    private List<CategoryChartResponse> topCategories;

    private List<TopMerchantResponse> topMerchants;
}