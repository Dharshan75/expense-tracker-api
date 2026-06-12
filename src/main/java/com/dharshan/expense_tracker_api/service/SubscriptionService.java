package com.dharshan.expense_tracker_api.service;

import com.dharshan.expense_tracker_api.dto.SubscriptionRequest;
import com.dharshan.expense_tracker_api.model.Subscription;
import com.dharshan.expense_tracker_api.model.SubscriptionType;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    // ===============================
    // GET SUBSCRIPTION
    // ===============================
    public Subscription getSubscription(User user) {

        return subscriptionRepository.findByUser(user)
                .orElseGet(() -> {

                    Subscription subscription = Subscription.builder()
                            .type(SubscriptionType.FREE)
                            .startDate(LocalDate.now())
                            .endDate(null)
                            .autoRenew(false)
                            .active(true)
                            .user(user)
                            .build();

                    return subscriptionRepository.save(subscription);
                });
    }

    // ===============================
    // UPGRADE SUBSCRIPTION
    // ===============================
    public Subscription upgradeSubscription(
            SubscriptionRequest request,
            User user) {

        Subscription subscription = getSubscription(user);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = null;

        switch (request.getType()) {

            case MONTHLY:
                endDate = startDate.plusMonths(1);
                break;

            case QUARTERLY:
                endDate = startDate.plusMonths(3);
                break;

            case HALF_YEARLY:
                endDate = startDate.plusMonths(6);
                break;

            case YEARLY:
                endDate = startDate.plusYears(1);
                break;

            case FREE:
                endDate = null;
                break;
        }

        subscription.setType(request.getType());
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setAutoRenew(request.getAutoRenew());
        subscription.setActive(true);

        return subscriptionRepository.save(subscription);
    }
}