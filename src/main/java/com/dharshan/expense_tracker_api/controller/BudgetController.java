package com.dharshan.expense_tracker_api.controller;

import com.dharshan.expense_tracker_api.dto.BudgetAlertResponse;
import com.dharshan.expense_tracker_api.dto.BudgetRequest;
import com.dharshan.expense_tracker_api.dto.BudgetResponse;
import com.dharshan.expense_tracker_api.dto.BudgetUsageResponse;
import com.dharshan.expense_tracker_api.model.Budget;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.service.BudgetService;
import com.dharshan.expense_tracker_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final UserService userService;

    // ===============================
    // GET CURRENT USER
    // ===============================
    private User getCurrentUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userService.getUserByEmail(email);
    }

    // ===============================
    // CREATE BUDGET
    // ===============================
    @PostMapping
    public BudgetResponse createBudget(
            @RequestBody BudgetRequest request) {

        User user = getCurrentUser();

        Budget budget = budgetService.createBudget(request, user);

        return BudgetResponse.builder()
                .id(budget.getId())
                .category(budget.getCategory())
                .amount(budget.getAmount())
                .month(budget.getMonth())
                .year(budget.getYear())
                .build();
    }

    // ===============================
    // GET ALL BUDGETS
    // ===============================
    @GetMapping
    public List<BudgetResponse> getBudgets() {

        User user = getCurrentUser();

        return budgetService.getBudgets(user)
                .stream()
                .map(budget -> BudgetResponse.builder()
                        .id(budget.getId())
                        .category(budget.getCategory())
                        .amount(budget.getAmount())
                        .month(budget.getMonth())
                        .year(budget.getYear())
                        .build())
                .toList();
    }

    // ===============================
    // GET BUDGET USAGE
    // ===============================
    @GetMapping("/usage")
    public List<BudgetUsageResponse> getBudgetUsage() {

        User user = getCurrentUser();

        return budgetService.getBudgetUsage(user);
    }

    // ===============================
    // GET BUDGET ALERTS
    // ===============================
    @GetMapping("/alerts")
    public List<BudgetAlertResponse> getBudgetAlerts() {

        User user = getCurrentUser();

        return budgetService.getBudgetAlerts(user);
    }
    // ===============================
// UPDATE BUDGET
// ===============================
    @PutMapping("/{id}")
    public BudgetResponse updateBudget(

            @PathVariable UUID id,

            @RequestBody BudgetRequest request) {

        User user = getCurrentUser();

        Budget budget =
                budgetService.updateBudget(
                        id,
                        request,
                        user
                );

        return BudgetResponse.builder()
                .id(budget.getId())
                .category(budget.getCategory())
                .amount(budget.getAmount())
                .month(budget.getMonth())
                .year(budget.getYear())
                .build();
    }

    // ===============================
// DELETE BUDGET
// ===============================
    @DeleteMapping("/{id}")
    public String deleteBudget(
            @PathVariable UUID id) {

        User user = getCurrentUser();

        budgetService.deleteBudget(id, user);

        return "Budget deleted successfully";
    }
}