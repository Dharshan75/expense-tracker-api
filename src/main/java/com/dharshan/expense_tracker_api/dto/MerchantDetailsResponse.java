package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MerchantDetailsResponse {

    private String merchant;

    private Double totalAmount;

    private List<CategoryChartResponse> topCategories;

    private List<TopPurposeResponse> topProducts;
}