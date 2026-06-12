package com.dharshan.expense_tracker_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmsParseResponse {

    private Boolean expense;

    private Boolean income;

    private Double amount;

    private String merchantName;

    private String transactionId;

    private String bankName;

    private String rawMessage;
}