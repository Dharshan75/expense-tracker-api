package com.dharshan.expense_tracker_api.service;

import com.dharshan.expense_tracker_api.dto.MonthlyTrendResponse;
import com.dharshan.expense_tracker_api.model.Expense;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import com.dharshan.expense_tracker_api.dto.CategoryChartResponse;
import com.dharshan.expense_tracker_api.model.ExpenseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dharshan.expense_tracker_api.dto.TopMerchantResponse;
import com.dharshan.expense_tracker_api.dto.TopPurposeResponse;
import com.dharshan.expense_tracker_api.dto.SpendingPatternResponse;
import com.dharshan.expense_tracker_api.dto.TopCategoryResponse;
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ExpenseRepository expenseRepository;

    public List<MonthlyTrendResponse> getMonthlyTrend(User user) {

        List<MonthlyTrendResponse> result = new ArrayList<>();

        for (Month month : Month.values()) {

            double total = expenseRepository.findByUser(user)
                    .stream()
                    .filter(expense ->
                            expense.getDate() != null &&
                                    expense.getDate().getMonth() == month)
                    .mapToDouble(Expense::getAmount)
                    .sum();

            result.add(
                    MonthlyTrendResponse.builder()
                            .month(month.name())
                            .amount(total)
                            .build()
            );
        }

        return result;
    }
    public List<CategoryChartResponse> getCategoryChart(User user) {

        Map<String, Double> categoryMap = new HashMap<>();

        for (Expense expense : expenseRepository.findByUser(user)) {

            if (expense.getStatus() != ExpenseStatus.COMPLETED) {
                continue;
            }

            String category = expense.getCategory();

            if (category == null) {
                category = "Uncategorized";
            }

            categoryMap.put(
                    category,
                    categoryMap.getOrDefault(category, 0.0)
                            + expense.getAmount()
            );
        }

        List<CategoryChartResponse> result = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {

            result.add(
                    CategoryChartResponse.builder()
                            .category(entry.getKey())
                            .amount(entry.getValue())
                            .build()
            );
        }

        return result;
    }
    public List<TopMerchantResponse> getTopMerchants(User user) {

        Map<String, Double> merchantMap = new HashMap<>();

        for (Expense expense : expenseRepository.findByUser(user)) {

            if (expense.getStatus() != ExpenseStatus.COMPLETED) {
                continue;
            }

            String merchant = expense.getMerchantName();

            if (merchant == null || merchant.isBlank()) {
                merchant = "Unknown";
            }
            merchantMap.put(
                    merchant,
                    merchantMap.getOrDefault(merchant, 0.0)
                            + expense.getAmount()
            );
        }

        List<TopMerchantResponse> result = new ArrayList<>();

        for (Map.Entry<String, Double> entry : merchantMap.entrySet()) {

            result.add(
                    TopMerchantResponse.builder()
                            .merchant(entry.getKey())
                            .amount(entry.getValue())
                            .build()
            );
        }

        result.sort((a, b) ->
                Double.compare(b.getAmount(), a.getAmount()));

        return result;
    }
    public List<TopPurposeResponse> getTopPurposes(User user) {

        Map<String, Double> purposeMap = new HashMap<>();

        for (Expense expense : expenseRepository.findByUser(user)) {

            if (expense.getStatus() != ExpenseStatus.COMPLETED) {
                continue;
            }

            String purpose = expense.getTitle();

            // fallback to category
            if (purpose == null || purpose.isBlank()) {
                purpose = expense.getCategory();
            }

            // final fallback
            if (purpose == null || purpose.isBlank()) {
                purpose = "Unknown";
            }

            purposeMap.put(
                    purpose,
                    purposeMap.getOrDefault(purpose, 0.0)
                            + expense.getAmount()
            );
        }

        List<TopPurposeResponse> result = new ArrayList<>();

        for (Map.Entry<String, Double> entry : purposeMap.entrySet()) {

            result.add(
                    TopPurposeResponse.builder()
                            .purpose(entry.getKey())
                            .amount(entry.getValue())
                            .build()
            );
        }

        result.sort((a, b) ->
                Double.compare(b.getAmount(), a.getAmount()));

        return result;
    }
    public SpendingPatternResponse getSpendingPattern(User user) {

        double totalExpense = 0;

        Map<String, Double> categoryMap = new HashMap<>();
        Map<String, Double> merchantMap = new HashMap<>();
        Map<String, Double> purposeMap = new HashMap<>();

        for (Expense expense : expenseRepository.findByUser(user)) {

            if (expense.getStatus() != ExpenseStatus.COMPLETED) {
                continue;
            }

            totalExpense += expense.getAmount();

            // CATEGORY
            String category = expense.getCategory();
            if (category != null) {
                categoryMap.put(
                        category,
                        categoryMap.getOrDefault(category, 0.0)
                                + expense.getAmount()
                );
            }

            // MERCHANT
            String merchant = expense.getMerchantName();
            if (merchant != null) {
                merchantMap.put(
                        merchant,
                        merchantMap.getOrDefault(merchant, 0.0)
                                + expense.getAmount()
                );
            }

            // PURPOSE
            String purpose = expense.getTitle();

            if (purpose == null || purpose.isBlank()) {
                purpose = expense.getCategory();
            }

            if (purpose != null) {
                purposeMap.put(
                        purpose,
                        purposeMap.getOrDefault(purpose, 0.0)
                                + expense.getAmount()
                );
            }
        }

        String highestCategory =
                categoryMap.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("None");

        String highestMerchant =
                merchantMap.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("None");

        String highestPurpose =
                purposeMap.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("None");

        long days =
                expenseRepository.findByUser(user)
                        .stream()
                        .filter(e -> e.getStatus() == ExpenseStatus.COMPLETED)
                        .map(Expense::getDate)
                        .filter(date -> date != null)
                        .distinct()
                        .count();

        double averageDailyExpense =
                days == 0 ? 0 : totalExpense / days;

        return SpendingPatternResponse.builder()
                .highestCategory(highestCategory)
                .highestMerchant(highestMerchant)
                .highestPurpose(highestPurpose)
                .averageDailyExpense(averageDailyExpense)
                .totalExpense(totalExpense)
                .build();
    }
    public TopCategoryResponse getTopCategory(User user) {

        Map<String, Double> categoryMap = new HashMap<>();

        for (Expense expense : expenseRepository.findByUser(user)) {

            if (expense.getStatus() != ExpenseStatus.COMPLETED) {
                continue;
            }

            String category = expense.getCategory();

            if (category == null || category.isBlank()) {
                category = "Uncategorized";
            }

            categoryMap.put(
                    category,
                    categoryMap.getOrDefault(category, 0.0)
                            + expense.getAmount()
            );
        }

        Map.Entry<String, Double> topCategory =
                categoryMap.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .orElse(null);

        if (topCategory == null) {

            return TopCategoryResponse.builder()
                    .category("None")
                    .amount(0.0)
                    .build();
        }

        return TopCategoryResponse.builder()
                .category(topCategory.getKey())
                .amount(topCategory.getValue())
                .build();
    }
}
