package com.dharshan.expense_tracker_api.dto;

import com.dharshan.expense_tracker_api.model.SubscriptionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class SubscriptionResponse {

    private UUID id;

    private SubscriptionType type;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean autoRenew;

    private Boolean active;
}