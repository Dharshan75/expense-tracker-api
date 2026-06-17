package com.dharshan.expense_tracker_api.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {

    private String currentPassword;

    private String newPassword;

}