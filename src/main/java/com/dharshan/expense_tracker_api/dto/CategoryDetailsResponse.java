package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryDetailsResponse {

    private String category;

    private Double totalAmount;

    private List<TopPurposeResponse> topProducts;

    private List<TopMerchantResponse> topMerchants;
}