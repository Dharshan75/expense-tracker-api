package com.dharshan.expense_tracker_api.repository;

import com.dharshan.expense_tracker_api.model.Expense;
import com.dharshan.expense_tracker_api.model.ExpenseStatus;
import com.dharshan.expense_tracker_api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository
        extends JpaRepository<Expense, Long> {

    // ===============================
    // BASIC
    // ===============================
    List<Expense> findByUser(User user);

    Page<Expense> findByUser(User user, Pageable pageable);

    List<Expense> findByUserAndCategory(
            User user,
            String category);

    List<Expense> findByUserAndStatus(
            User user,
            ExpenseStatus status);

    // ===============================
    // DATE FILTERS
    // ===============================
    List<Expense> findByUserAndDate(
            User user,
            LocalDate date);

    List<Expense> findByUserAndDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate);

    List<Expense> findByUserAndCategoryAndDateBetween(
            User user,
            String category,
            LocalDate startDate,
            LocalDate endDate);

    // ===============================
    // SMS DUPLICATE CHECK
    // ===============================
    Optional<Expense> findByTransactionId(
            String transactionId);

    // ===============================
    // SEARCH
    // ===============================
    List<Expense>
    findByUserAndTitleContainingIgnoreCaseOrUserAndCategoryContainingIgnoreCase(
            User user,
            String titleKeyword,
            User sameUser,
            String categoryKeyword);
}