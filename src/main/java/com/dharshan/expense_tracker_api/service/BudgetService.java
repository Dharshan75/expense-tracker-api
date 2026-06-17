package com.dharshan.expense_tracker_api.service;

import com.dharshan.expense_tracker_api.dto.BudgetAlertResponse;
import com.dharshan.expense_tracker_api.dto.BudgetRequest;
import com.dharshan.expense_tracker_api.dto.BudgetUsageResponse;
import com.dharshan.expense_tracker_api.model.Budget;
import com.dharshan.expense_tracker_api.model.Expense;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.repository.BudgetRepository;
import com.dharshan.expense_tracker_api.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    // ===============================
    // CREATE BUDGET
    // ===============================
    // ===============================
// CREATE BUDGET
// ===============================
    public Budget createBudget(BudgetRequest request, User user) {

        Optional<Budget> existingBudget =
                budgetRepository.findByUserAndCategoryAndMonthAndYear(

                        user,
                        request.getCategory(),
                        request.getMonth(),
                        request.getYear()

                );

        if (existingBudget.isPresent()) {

            throw new RuntimeException(
                    "Budget already exists for this category and month"
            );

        }

        Budget budget = Budget.builder()

                .category(request.getCategory())
                .amount(request.getAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .user(user)

                .build();

        return budgetRepository.save(budget);

    }

    // ===============================
    // GET ALL BUDGETS
    // ===============================
    public List<Budget> getBudgets(User user) {
        return budgetRepository.findByUser(user);
    }

    // ===============================
    // GET BUDGET USAGE
    // ===============================
    public List<BudgetUsageResponse> getBudgetUsage(User user) {

        List<BudgetUsageResponse> responses = new ArrayList<>();

        List<Budget> budgets = budgetRepository.findByUser(user);

        for (Budget budget : budgets) {

            LocalDate startDate = LocalDate.of(
                    budget.getYear(),
                    budget.getMonth(),
                    1
            );

            LocalDate endDate = startDate.withDayOfMonth(
                    startDate.lengthOfMonth()
            );

            double spent = 0;

            List<Expense> expenses =
                    expenseRepository.findByUserAndCategoryAndDateBetween(
                            user,
                            budget.getCategory(),
                            startDate,
                            endDate
                    );

            for (Expense expense : expenses) {
                spent += expense.getAmount();
            }

            double remaining = budget.getAmount() - spent;

            double percentage = (spent / budget.getAmount()) * 100;

            responses.add(
                    BudgetUsageResponse.builder()
                            .id(budget.getId())
                            .category(budget.getCategory())
                            .budgetAmount(budget.getAmount())
                            .spentAmount(spent)
                            .remainingAmount(remaining)
                            .percentageUsed(percentage)
                            .exceeded(spent > budget.getAmount())
                            .build()
            );
        }

        return responses;
    }

    // ===============================
    // GET BUDGET ALERTS
    // ===============================
    public List<BudgetAlertResponse> getBudgetAlerts(User user) {

        List<BudgetAlertResponse> alerts = new ArrayList<>();

        List<BudgetUsageResponse> usages = getBudgetUsage(user);

        for (BudgetUsageResponse usage : usages) {

            String message = null;

            if (usage.getPercentageUsed() >= 100) {

                message = "Budget exceeded";

            } else if (usage.getPercentageUsed() >= 80) {

                message = "Budget reached 80%";
            }

            if (message != null) {

                alerts.add(
                        BudgetAlertResponse.builder()
                                .category(usage.getCategory())
                                .percentageUsed(usage.getPercentageUsed())
                                .message(message)
                                .exceeded(usage.getExceeded())
                                .build()
                );
            }
        }

        return alerts;
    }
    // ===============================
// UPDATE BUDGET
// ===============================
    public Budget updateBudget(
            UUID id,
            BudgetRequest request,
            User user) {

        Budget budget = budgetRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Budget not found"));

        budget.setAmount(request.getAmount());

        return budgetRepository.save(budget);
    }

    // ===============================
// DELETE BUDGET
// ===============================
    public void deleteBudget(
            UUID id,
            User user) {

        Budget budget = budgetRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Budget not found"));

        budgetRepository.delete(budget);
    }
}