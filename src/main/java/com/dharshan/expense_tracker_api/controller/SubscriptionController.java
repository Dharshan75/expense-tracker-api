package com.dharshan.expense_tracker_api.controller;

import com.dharshan.expense_tracker_api.dto.SubscriptionRequest;
import com.dharshan.expense_tracker_api.dto.SubscriptionResponse;
import com.dharshan.expense_tracker_api.model.Subscription;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.service.SubscriptionService;
import com.dharshan.expense_tracker_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
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
    // GET SUBSCRIPTION
    // ===============================
    @GetMapping
    public SubscriptionResponse getSubscription() {

        System.out.println("STEP 1");

        User user = getCurrentUser();

        System.out.println("STEP 2");

        Subscription subscription =
                subscriptionService.getSubscription(user);

        System.out.println("STEP 3");

        return mapToResponse(subscription);
    }

    // ===============================
    // UPGRADE SUBSCRIPTION
    // ===============================
    @PutMapping("/upgrade")
    public SubscriptionResponse upgradeSubscription(
            @RequestBody SubscriptionRequest request) {

        User user = getCurrentUser();

        Subscription subscription =
                subscriptionService.upgradeSubscription(request, user);

        return mapToResponse(subscription);
    }

    // ===============================
    // ENTITY → DTO
    // ===============================
    private SubscriptionResponse mapToResponse(
            Subscription subscription) {

        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .type(subscription.getType())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .autoRenew(subscription.getAutoRenew())
                .active(subscription.getActive())
                .build();
    }

}