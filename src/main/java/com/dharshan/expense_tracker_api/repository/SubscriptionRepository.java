package com.dharshan.expense_tracker_api.repository;

import com.dharshan.expense_tracker_api.model.Subscription;
import com.dharshan.expense_tracker_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByUser(User user);

}