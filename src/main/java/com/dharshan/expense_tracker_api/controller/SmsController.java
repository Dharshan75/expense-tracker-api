package com.dharshan.expense_tracker_api.controller;

import com.dharshan.expense_tracker_api.dto.SmsExpenseResponse;
import com.dharshan.expense_tracker_api.dto.SmsParseRequest;
import com.dharshan.expense_tracker_api.dto.SmsParseResponse;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.service.ExpenseService;
import com.dharshan.expense_tracker_api.service.SmsParserService;
import com.dharshan.expense_tracker_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsParserService smsParserService;
    private final ExpenseService expenseService;
    private final UserService userService;

    // ===============================
    // PARSE SMS ONLY
    // ===============================
    @PostMapping("/parse")
    public SmsParseResponse parseSms(
            @RequestBody SmsParseRequest request) {

        return smsParserService.parse(
                request.getMessage()
        );
    }

    // ===============================
    // CREATE EXPENSE FROM SMS
    // ===============================
    @PostMapping("/create-expense")
    public SmsExpenseResponse createExpense(
            @RequestBody SmsParseRequest request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userService.getUserByEmail(email);

        SmsParseResponse response =
                smsParserService.parse(request.getMessage());

        expenseService.createSmsExpense(response, user);

        return SmsExpenseResponse.builder()
                .created(true)
                .message("Expense created successfully")
                .build();
    }
}