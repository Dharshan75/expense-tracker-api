package com.dharshan.expense_tracker_api.repository;

import com.dharshan.expense_tracker_api.model.RecurringExpense;
import com.dharshan.expense_tracker_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

import java.util.Optional;

public interface RecurringExpenseRepository
        extends JpaRepository<RecurringExpense, UUID> {

    List<RecurringExpense> findByUser(User user);
    void deleteByIdAndUser(UUID id, User user);
    Optional<RecurringExpense> findByIdAndUser(
            UUID id,
            User user);
}