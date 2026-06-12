package com.dharshan.expense_tracker_api.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}