package com.dharshan.expense_tracker_api.controller;

import com.dharshan.expense_tracker_api.dto.RecurringExpenseRequest;
import com.dharshan.expense_tracker_api.dto.RecurringExpenseResponse;
import com.dharshan.expense_tracker_api.model.RecurringExpense;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.service.RecurringExpenseService;
import com.dharshan.expense_tracker_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recurring-expenses")
@RequiredArgsConstructor
public class RecurringExpenseController {

    private final RecurringExpenseService recurringExpenseService;
    private final UserService userService;

    // ===============================
    // GET CURRENT USER
    // ===============================
    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userService.getUserByEmail(email);
    }

    // ===============================
    // CREATE RECURRING EXPENSE
    // ===============================
    @PostMapping
    public RecurringExpenseResponse createRecurringExpense(
            @RequestBody RecurringExpenseRequest request) {

        User user = getCurrentUser();

        RecurringExpense recurringExpense =
                recurringExpenseService.createRecurringExpense(
                        request,
                        user
                );

        return RecurringExpenseResponse.builder()
                .id(recurringExpense.getId())
                .title(recurringExpense.getTitle())
                .category(recurringExpense.getCategory())
                .amount(recurringExpense.getAmount())
                .frequency(recurringExpense.getFrequency())
                .build();
    }

    // ===============================
    // GET ALL RECURRING EXPENSES
    // ===============================
    @GetMapping
    public List<RecurringExpenseResponse> getRecurringExpenses() {

        User user = getCurrentUser();

        return recurringExpenseService
                .getRecurringExpenses(user)
                .stream()
                .map(recurringExpense ->
                        RecurringExpenseResponse.builder()
                                .id(recurringExpense.getId())
                                .title(recurringExpense.getTitle())
                                .category(recurringExpense.getCategory())
                                .amount(recurringExpense.getAmount())
                                .frequency(recurringExpense.getFrequency())
                                .build()
                )
                .toList();
    }
    @DeleteMapping("/{id}")
    public void deleteRecurringExpense(
            @PathVariable UUID id) {

        User user = getCurrentUser();

        recurringExpenseService
                .deleteRecurringExpense(id, user);

    }
    @PutMapping("/{id}")
    public RecurringExpenseResponse updateRecurringExpense(

            @PathVariable UUID id,

            @RequestBody
            RecurringExpenseRequest request) {

        User user = getCurrentUser();

        RecurringExpense expense =
                recurringExpenseService
                        .updateRecurringExpense(
                                id,
                                request,
                                user
                        );

        return RecurringExpenseResponse.builder()
                .id(expense.getId())
                .title(expense.getTitle())
                .category(expense.getCategory())
                .amount(expense.getAmount())
                .frequency(expense.getFrequency())
                .build();
    }
}