package com.dharshan.expense_tracker_api.service;

import com.dharshan.expense_tracker_api.dto.ExpenseRequest;
import com.dharshan.expense_tracker_api.dto.SmsParseResponse;
import com.dharshan.expense_tracker_api.model.*;
import com.dharshan.expense_tracker_api.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryPredictionService categoryPredictionService;

    // ===============================
    // ADD EXPENSE
    // ===============================
    public Expense addExpense(ExpenseRequest request, User user) {

        // Prevent duplicate SMS transactions
        if (request.getTransactionId() != null &&
                expenseRepository.findByTransactionId(
                        request.getTransactionId()).isPresent()) {

            throw new RuntimeException("Duplicate transaction");
        }

        Expense expense = Expense.builder()
                .title(request.getTitle())
                .amount(request.getAmount())
                .category(request.getCategory())
                .description(request.getDescription())
                .date(
                        request.getDate() != null
                                ? request.getDate()
                                : LocalDate.now()
                )
                .merchantName(request.getMerchantName())
                .source(request.getSource())
                .transactionId(request.getTransactionId())
                .user(user)
                .status(ExpenseStatus.COMPLETED)
                .build();

        return expenseRepository.save(expense);
    }

    // ===============================
    // GET ALL EXPENSES
    // ===============================
    public List<Expense> getUserExpenses(User user) {
        return expenseRepository.findByUser(user);
    }

    // ===============================
    // FILTER BY CATEGORY
    // ===============================
    public List<Expense> getExpensesByCategory(User user, String category) {
        return expenseRepository.findByUserAndCategory(user, category);
    }

    // ===============================
    // GET UNCATEGORIZED EXPENSES
    // ===============================
    public List<Expense> getUncategorizedExpenses(User user) {
        return expenseRepository.findByUserAndStatus(
                user,
                ExpenseStatus.PENDING_REVIEW
        );
    }

    // ===============================
    // CATEGORIZE EXPENSE
    // ===============================
    public Expense categorizeExpense(
            Long expenseId,
            String category,
            User user) {

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found"));

        System.out.println("Expense User ID = " + expense.getUser().getId());
        System.out.println("Current User ID = " + user.getId());

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        expense.setCategory(category);
        expense.setStatus(ExpenseStatus.COMPLETED);

        return expenseRepository.save(expense);
    }

    // ===============================
    // TOTAL EXPENSE
    // ===============================
    public Double getTotalExpense(User user) {

        return expenseRepository.findByUser(user)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // ===============================
    // DAILY EXPENSE
    // ===============================
    public Double getDailyExpense(User user, LocalDate date) {

        return expenseRepository.findByUserAndDate(user, date)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // ===============================
    // WEEKLY EXPENSE
    // ===============================
    public Double getWeeklyExpense(User user) {

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        return expenseRepository.findByUserAndDateBetween(
                        user,
                        startDate,
                        endDate)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // ===============================
    // MONTHLY EXPENSE
    // ===============================
    public Double getMonthlyExpense(
            User user,
            int month,
            int year) {

        return expenseRepository.findByUser(user)
                .stream()
                .filter(expense ->
                        expense.getDate().getMonthValue() == month &&
                                expense.getDate().getYear() == year)
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // ===============================
    // YEARLY EXPENSE
    // ===============================
    public Double getYearlyExpense(
            User user,
            int year) {

        return expenseRepository.findByUser(user)
                .stream()
                .filter(expense ->
                        expense.getDate().getYear() == year)
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // ===============================
    // CATEGORY SUMMARY
    // ===============================
    public Map<String, Double> getCategorySummary(User user) {

        Map<String, Double> summary = new HashMap<>();

        for (Expense expense : expenseRepository.findByUser(user)) {

            summary.put(
                    expense.getCategory(),
                    summary.getOrDefault(
                            expense.getCategory(),
                            0.0
                    ) + expense.getAmount()
            );
        }

        return summary;
    }

    // ===============================
    // CREATE EXPENSE FROM SMS
    // ===============================
    public Expense createSmsExpense(
            SmsParseResponse smsResponse,
            User user) {

        // Ignore income messages
        if (Boolean.TRUE.equals(smsResponse.getIncome())) {
            return null;
        }

        // Ignore non-expense messages
        if (!Boolean.TRUE.equals(smsResponse.getExpense())) {
            return null;
        }

        // Prevent duplicate transactions
        if (smsResponse.getTransactionId() != null &&
                expenseRepository.findByTransactionId(
                        smsResponse.getTransactionId()).isPresent()) {

            throw new RuntimeException("Duplicate transaction");
        }

        Expense expense = Expense.builder()
                .title(null)
                .amount(smsResponse.getAmount())
                .category(null)
                .suggestedCategory(
                        categoryPredictionService.predictCategory(
                                smsResponse.getMerchantName()
                        )
                )
                .description("Automatically created from SMS")
                .date(LocalDate.now())
                .merchantName(smsResponse.getMerchantName())
                .transactionId(smsResponse.getTransactionId())
                .source(ExpenseSource.SMS)
                .status(ExpenseStatus.PENDING_REVIEW)
                .user(user)
                .build();

        return expenseRepository.save(expense);
    }

    // ===============================
// COMPLETE EXPENSE
// ===============================
    public Expense completeExpense(
            Long expenseId,
            String title,
            String category,
            User user) {

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        expense.setTitle(title);
        expense.setCategory(category);
        expense.setStatus(ExpenseStatus.COMPLETED);

        return expenseRepository.save(expense);
    }

    // ===============================
    // DELETE EXPENSE
    // ===============================
    public void deleteExpense(Long id, User user) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        expenseRepository.delete(expense);
    }
}