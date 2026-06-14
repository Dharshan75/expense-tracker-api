package com.dharshan.expense_tracker_api.service;

import com.dharshan.expense_tracker_api.dto.MonthlyTrendResponse;
import com.dharshan.expense_tracker_api.model.Expense;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

import com.dharshan.expense_tracker_api.dto.AiInsightResponse;
import com.dharshan.expense_tracker_api.dto.CategoryDetailsResponse;
import com.dharshan.expense_tracker_api.dto.MerchantDetailsResponse;
import com.dharshan.expense_tracker_api.dto.PurposeDetailsResponse;

import com.dharshan.expense_tracker_api.dto.MonthComparisonResponse;
import com.dharshan.expense_tracker_api.dto.BudgetAlertResponse;
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ExpenseRepository expenseRepository;
    private final BudgetService budgetService;
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
    public AiInsightResponse getAiInsights(User user) {

        List<String> insights = new ArrayList<>();

        // Reuse existing analytics
        TopCategoryResponse topCategory = getTopCategory(user);

        List<TopMerchantResponse> merchants = getTopMerchants(user);

        List<TopPurposeResponse> purposes = getTopPurposes(user);

        SpendingPatternResponse pattern = getSpendingPattern(user);

        MonthComparisonResponse comparison =
                getMonthComparison(user);
        List<BudgetAlertResponse> budgetAlerts =
                budgetService.getBudgetAlerts(user);
        // Highest category
        insights.add(
                topCategory.getCategory()
                        + " is your highest spending category."
        );

        // Highest merchant
        if (!merchants.isEmpty()) {

            insights.add(
                    merchants.get(0).getMerchant()
                            + " is your top merchant."
            );
        }

        // Highest purpose
        if (!purposes.isEmpty()) {

            insights.add(
                    purposes.get(0).getPurpose()
                            + " is your highest expense purpose."
            );
        }

        // Average daily expense
        insights.add(
                "Your average daily expense is ₹"
                        + Math.round(
                        pattern.getAverageDailyExpense() * 100.0
                ) / 100.0
        );

        // Total expense
        insights.add(
                "You spent ₹"
                        + pattern.getTotalExpense()
                        + " in total."
        );

        if (!comparison.getTrend().equals("UNCHANGED")) {

            insights.add(
                    "Your spending "
                            + comparison.getTrend().toLowerCase()
                            + " by "
                            + comparison.getPercentageChange()
                            + "% compared to last month."
            );
        }

        for (BudgetAlertResponse alert : budgetAlerts) {

            insights.add(
                    alert.getCategory()
                            + " budget: "
                            + alert.getMessage()
            );
        }
        return AiInsightResponse.builder()
                .insights(insights)
                .build();

    }
    public CategoryDetailsResponse getCategoryDetails(
            String category,
            User user) {

        double totalAmount = 0;

        Map<String, Double> purposeMap = new HashMap<>();
        Map<String, Double> merchantMap = new HashMap<>();

        for (Expense expense : expenseRepository.findByUser(user)) {

            if (expense.getStatus() != ExpenseStatus.COMPLETED) {
                continue;
            }

            if (!category.equalsIgnoreCase(expense.getCategory())) {
                continue;
            }

            totalAmount += expense.getAmount();

            // PURPOSE
            String purpose = expense.getTitle();

            if (purpose == null || purpose.isBlank()) {
                purpose = category;
            }

            purposeMap.put(
                    purpose,
                    purposeMap.getOrDefault(purpose, 0.0)
                            + expense.getAmount()
            );

            // MERCHANT
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

        List<TopPurposeResponse> topProducts = new ArrayList<>();

        for (Map.Entry<String, Double> entry : purposeMap.entrySet()) {

            topProducts.add(
                    TopPurposeResponse.builder()
                            .purpose(entry.getKey())
                            .amount(entry.getValue())
                            .build()
            );
        }

        topProducts.sort(
                (a, b) -> Double.compare(
                        b.getAmount(),
                        a.getAmount()
                )
        );

        List<TopMerchantResponse> topMerchants = new ArrayList<>();

        for (Map.Entry<String, Double> entry : merchantMap.entrySet()) {

            topMerchants.add(
                    TopMerchantResponse.builder()
                            .merchant(entry.getKey())
                            .amount(entry.getValue())
                            .build()
            );
        }

        topMerchants.sort(
                (a, b) -> Double.compare(
                        b.getAmount(),
                        a.getAmount()
                )
        );

        return CategoryDetailsResponse.builder()
                .category(category)
                .totalAmount(totalAmount)
                .topProducts(topProducts)
                .topMerchants(topMerchants)
                .build();
    }
    public MerchantDetailsResponse getMerchantDetails(
            String merchant,
            User user) {

        double totalAmount = 0;

        Map<String, Double> categoryMap = new HashMap<>();
        Map<String, Double> purposeMap = new HashMap<>();

        for (Expense expense : expenseRepository.findByUser(user)) {

            if (expense.getStatus() != ExpenseStatus.COMPLETED) {
                continue;
            }

            String expenseMerchant = expense.getMerchantName();

            if (expenseMerchant == null ||
                    !merchant.equalsIgnoreCase(expenseMerchant)) {
                continue;
            }

            totalAmount += expense.getAmount();

            // CATEGORY
            String category = expense.getCategory();

            if (category == null || category.isBlank()) {
                category = "Uncategorized";
            }

            categoryMap.put(
                    category,
                    categoryMap.getOrDefault(category, 0.0)
                            + expense.getAmount()
            );

            // PURPOSE
            String purpose = expense.getTitle();

            if (purpose == null || purpose.isBlank()) {
                purpose = category;
            }

            purposeMap.put(
                    purpose,
                    purposeMap.getOrDefault(purpose, 0.0)
                            + expense.getAmount()
            );
        }

        List<CategoryChartResponse> topCategories = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {

            topCategories.add(
                    CategoryChartResponse.builder()
                            .category(entry.getKey())
                            .amount(entry.getValue())
                            .build()
            );
        }

        topCategories.sort(
                (a, b) -> Double.compare(
                        b.getAmount(),
                        a.getAmount()
                )
        );

        List<TopPurposeResponse> topProducts = new ArrayList<>();

        for (Map.Entry<String, Double> entry : purposeMap.entrySet()) {

            topProducts.add(
                    TopPurposeResponse.builder()
                            .purpose(entry.getKey())
                            .amount(entry.getValue())
                            .build()
            );
        }

        topProducts.sort(
                (a, b) -> Double.compare(
                        b.getAmount(),
                        a.getAmount()
                )
        );

        return MerchantDetailsResponse.builder()
                .merchant(merchant)
                .totalAmount(totalAmount)
                .topCategories(topCategories)
                .topProducts(topProducts)
                .build();
    }
    public PurposeDetailsResponse getPurposeDetails(
            String purpose,
            User user) {

        double totalAmount = 0;

        Map<String, Double> categoryMap = new HashMap<>();
        Map<String, Double> merchantMap = new HashMap<>();

        for (Expense expense : expenseRepository.findByUser(user)) {

            if (expense.getStatus() != ExpenseStatus.COMPLETED) {
                continue;
            }

            String expensePurpose = expense.getTitle();

            // Same fallback used in Top Purpose Analytics
            if (expensePurpose == null || expensePurpose.isBlank()) {
                expensePurpose = expense.getCategory();
            }

            if (expensePurpose == null ||
                    !purpose.equalsIgnoreCase(expensePurpose)) {
                continue;
            }

            totalAmount += expense.getAmount();

            // CATEGORY
            String category = expense.getCategory();

            if (category == null || category.isBlank()) {
                category = "Uncategorized";
            }

            categoryMap.put(
                    category,
                    categoryMap.getOrDefault(category, 0.0)
                            + expense.getAmount()
            );

            // MERCHANT
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

        List<CategoryChartResponse> topCategories = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {

            topCategories.add(
                    CategoryChartResponse.builder()
                            .category(entry.getKey())
                            .amount(entry.getValue())
                            .build()
            );
        }

        topCategories.sort(
                (a, b) -> Double.compare(
                        b.getAmount(),
                        a.getAmount()
                )
        );

        List<TopMerchantResponse> topMerchants = new ArrayList<>();

        for (Map.Entry<String, Double> entry : merchantMap.entrySet()) {

            topMerchants.add(
                    TopMerchantResponse.builder()
                            .merchant(entry.getKey())
                            .amount(entry.getValue())
                            .build()
            );
        }

        topMerchants.sort(
                (a, b) -> Double.compare(
                        b.getAmount(),
                        a.getAmount()
                )
        );

        return PurposeDetailsResponse.builder()
                .purpose(purpose)
                .totalAmount(totalAmount)
                .topCategories(topCategories)
                .topMerchants(topMerchants)
                .build();
    }
    public MonthComparisonResponse getMonthComparison(User user) {

        LocalDate now = LocalDate.now();

        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        LocalDate previousMonthDate = now.minusMonths(1);

        int previousMonth = previousMonthDate.getMonthValue();
        int previousYear = previousMonthDate.getYear();

        double currentMonthExpense = 0;
        double previousMonthExpense = 0;

        for (Expense expense : expenseRepository.findByUser(user)) {

            if (expense.getStatus() != ExpenseStatus.COMPLETED) {
                continue;
            }

            if (expense.getDate() == null) {
                continue;
            }

            int expenseMonth = expense.getDate().getMonthValue();
            int expenseYear = expense.getDate().getYear();

            if (expenseMonth == currentMonth &&
                    expenseYear == currentYear) {

                currentMonthExpense += expense.getAmount();
            }

            if (expenseMonth == previousMonth &&
                    expenseYear == previousYear) {

                previousMonthExpense += expense.getAmount();
            }
        }

        double percentageChange = 0;

        if (previousMonthExpense > 0) {

            percentageChange =
                    ((currentMonthExpense - previousMonthExpense)
                            / previousMonthExpense) * 100;
        }

        String trend;

        if (currentMonthExpense > previousMonthExpense) {
            trend = "INCREASED";
        } else if (currentMonthExpense < previousMonthExpense) {
            trend = "DECREASED";
        } else {
            trend = "UNCHANGED";
        }

        return MonthComparisonResponse.builder()
                .currentMonthExpense(currentMonthExpense)
                .previousMonthExpense(previousMonthExpense)
                .percentageChange(
                        Math.round(percentageChange * 100.0) / 100.0
                )
                .trend(trend)
                .build();
    }
}
