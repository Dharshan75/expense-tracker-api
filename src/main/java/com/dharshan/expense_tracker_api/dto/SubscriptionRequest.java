package com.dharshan.expense_tracker_api.dto;

import com.dharshan.expense_tracker_api.model.SubscriptionType;
import lombok.Data;

@Data
public class SubscriptionRequest {

    private SubscriptionType type;

    private Boolean autoRenew;
}