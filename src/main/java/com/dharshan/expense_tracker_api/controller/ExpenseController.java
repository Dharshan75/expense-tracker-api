package com.dharshan.expense_tracker_api.controller;

import com.dharshan.expense_tracker_api.dto.ExpenseRequest;
import com.dharshan.expense_tracker_api.dto.ExpenseResponse;
import com.dharshan.expense_tracker_api.model.Expense;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.service.ExpenseService;
import com.dharshan.expense_tracker_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.dharshan.expense_tracker_api.dto.ExpenseCompleteRequest;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
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
    // ADD EXPENSE
    // ===============================
    @PostMapping
    public ExpenseResponse addExpense(
            @Valid @RequestBody ExpenseRequest request) {

        User user = getCurrentUser();

        Expense expense = expenseService.addExpense(request, user);

        return mapToResponse(expense);
    }

    // ===============================
    // GET ALL EXPENSES
    // ===============================
    @GetMapping
    public List<ExpenseResponse> getExpenses(
            @RequestParam(required = false) String category) {

        User user = getCurrentUser();

        List<Expense> expenses;

        if (category != null) {
            expenses = expenseService.getExpensesByCategory(user, category);
        } else {
            expenses = expenseService.getUserExpenses(user);
        }

        return expenses.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ===============================
    // GET PENDING REVIEW EXPENSES
    // ===============================
    @GetMapping("/uncategorized")
    public List<ExpenseResponse> getUncategorizedExpenses() {

        User user = getCurrentUser();

        return expenseService.getUncategorizedExpenses(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ===============================
    // CATEGORIZE EXPENSE
    // ===============================
    @PutMapping("/{id}/categorize")
    public ExpenseResponse categorizeExpense(
            @PathVariable Long id,
            @RequestParam String category) {

        User user = getCurrentUser();

        Expense expense =
                expenseService.categorizeExpense(id, category, user);

        return mapToResponse(expense);
    }

    // ===============================
    // TOTAL EXPENSE
    // ===============================
    @GetMapping("/total")
    public Double getTotalExpense() {

        User user = getCurrentUser();

        return expenseService.getTotalExpense(user);
    }

    // ===============================
    // DAILY EXPENSE
    // ===============================
    @GetMapping("/daily")
    public Double getDailyExpense(
            @RequestParam LocalDate date) {

        User user = getCurrentUser();

        return expenseService.getDailyExpense(user, date);
    }

    // ===============================
    // WEEKLY EXPENSE
    // ===============================
    @GetMapping("/weekly")
    public Double getWeeklyExpense() {

        User user = getCurrentUser();

        return expenseService.getWeeklyExpense(user);
    }

    // ===============================
    // MONTHLY EXPENSE
    // ===============================
    @GetMapping("/monthly")
    public Double getMonthlyExpense(
            @RequestParam int month,
            @RequestParam int year) {

        User user = getCurrentUser();

        return expenseService.getMonthlyExpense(user, month, year);
    }

    // ===============================
    // YEARLY EXPENSE
    // ===============================
    @GetMapping("/yearly")
    public Double getYearlyExpense(
            @RequestParam int year) {

        User user = getCurrentUser();

        return expenseService.getYearlyExpense(user, year);
    }

    // ===============================
    // CATEGORY SUMMARY
    // ===============================
    @GetMapping("/category-summary")
    public Map<String, Double> getCategorySummary() {

        User user = getCurrentUser();

        return expenseService.getCategorySummary(user);
    }

    // ===============================
// COMPLETE EXPENSE
// ===============================
    @PutMapping("/{id}/complete")
    public ExpenseResponse completeExpense(
            @PathVariable Long id,
            @RequestBody ExpenseCompleteRequest request) {

        User user = getCurrentUser();

        Expense expense = expenseService.completeExpense(
                id,
                request.getTitle(),
                request.getCategory(),
                user
        );

        return mapToResponse(expense);
    }

    // ===============================
    // DELETE EXPENSE
    // ===============================
    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Long id) {

        User user = getCurrentUser();

        expenseService.deleteExpense(id, user);

        return "Expense deleted successfully";
    }
    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }
    // ===============================
    // ENTITY → DTO
    // ===============================
    private ExpenseResponse mapToResponse(Expense expense) {

        return ExpenseResponse.builder()
                .id(expense.getId())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .suggestedCategory(expense.getSuggestedCategory())
                .description(expense.getDescription())
                .date(expense.getDate())
                .status(expense.getStatus())
                .merchantName(expense.getMerchantName())
                .source(expense.getSource())
                .transactionId(expense.getTransactionId())
                .build();
    }
}