package com.dharshan.expense_tracker_api.repository;

import com.dharshan.expense_tracker_api.model.Expense;
import com.dharshan.expense_tracker_api.model.ExpenseStatus;
import com.dharshan.expense_tracker_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // All expenses of a user
    List<Expense> findByUser(User user);

    // Filter by category
    List<Expense> findByUserAndCategory(User user, String category);

    // Filter by status
    List<Expense> findByUserAndStatus(User user, ExpenseStatus status);

    // Daily expenses
    List<Expense> findByUserAndDate(User user, LocalDate date);

    // Date range
    List<Expense> findByUserAndDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    // Category + Date range
    List<Expense> findByUserAndCategoryAndDateBetween(
            User user,
            String category,
            LocalDate startDate,
            LocalDate endDate
    );

    // Duplicate transaction protection
    Optional<Expense> findByTransactionId(String transactionId);
}