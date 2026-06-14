package com.dharshan.expense_tracker_api.controller;

import com.dharshan.expense_tracker_api.service.AnalyticsService;
import com.dharshan.expense_tracker_api.service.UserService;
import com.dharshan.expense_tracker_api.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.dharshan.expense_tracker_api.dto.MonthlyTrendResponse;
import java.util.List;
import com.dharshan.expense_tracker_api.dto.CategoryChartResponse;
import com.dharshan.expense_tracker_api.dto.TopMerchantResponse;
import com.dharshan.expense_tracker_api.dto.TopPurposeResponse;
import com.dharshan.expense_tracker_api.dto.SpendingPatternResponse;
import com.dharshan.expense_tracker_api.dto.TopCategoryResponse;
import com.dharshan.expense_tracker_api.dto.AiInsightResponse;
import com.dharshan.expense_tracker_api.dto.CategoryDetailsResponse;
import com.dharshan.expense_tracker_api.dto.MerchantDetailsResponse;
import com.dharshan.expense_tracker_api.dto.PurposeDetailsResponse;
import com.dharshan.expense_tracker_api.dto.MonthComparisonResponse;
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor

public class AnalyticsController {
    @GetMapping("/monthly-trend")
    public List<MonthlyTrendResponse> getMonthlyTrend() {

        User user = getCurrentUser();

        return analyticsService.getMonthlyTrend(user);
    }
    @GetMapping("/category-chart")
    public List<CategoryChartResponse> getCategoryChart() {

        User user = getCurrentUser();

        return analyticsService.getCategoryChart(user);
    }
    @GetMapping("/top-merchants")
    public List<TopMerchantResponse> getTopMerchants() {

        User user = getCurrentUser();

        return analyticsService.getTopMerchants(user);
    }
    @GetMapping("/top-purposes")
    public List<TopPurposeResponse> getTopPurposes() {

        User user = getCurrentUser();

        return analyticsService.getTopPurposes(user);
    }
    @GetMapping("/spending-pattern")
    public SpendingPatternResponse getSpendingPattern() {

        User user = getCurrentUser();

        return analyticsService.getSpendingPattern(user);
    }
    @GetMapping("/top-category")
    public TopCategoryResponse getTopCategory() {

        User user = getCurrentUser();

        return analyticsService.getTopCategory(user);
    }
    @GetMapping("/ai-insights")
    public AiInsightResponse getAiInsights() {

        User user = getCurrentUser();

        return analyticsService.getAiInsights(user);
    }
    @GetMapping("/category/{category}")
    public CategoryDetailsResponse getCategoryDetails(
            @PathVariable String category) {

        User user = getCurrentUser();

        return analyticsService.getCategoryDetails(
                category,
                user
        );
    }
    @GetMapping("/merchant/{merchant}")
    public MerchantDetailsResponse getMerchantDetails(
            @PathVariable String merchant) {

        User user = getCurrentUser();

        return analyticsService.getMerchantDetails(
                merchant,
                user
        );
    }
    @GetMapping("/purpose/{purpose}")
    public PurposeDetailsResponse getPurposeDetails(
            @PathVariable String purpose) {

        User user = getCurrentUser();

        return analyticsService.getPurposeDetails(
                purpose,
                user
        );
    }
    @GetMapping("/month-comparison")
    public MonthComparisonResponse getMonthComparison() {

        User user = getCurrentUser();

        return analyticsService.getMonthComparison(user);
    }
    private final AnalyticsService analyticsService;
    private final UserService userService;

    private User getCurrentUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userService.getUserByEmail(email);
    }
}