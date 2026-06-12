package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CategoryResponse {

    private UUID id;
    private String name;
    private Boolean defaultCategory;
}