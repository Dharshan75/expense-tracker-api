package com.dharshan.expense_tracker_api.repository;

import com.dharshan.expense_tracker_api.model.Budget;
import com.dharshan.expense_tracker_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    List<Budget> findByUser(User user);

    Optional<Budget> findByUserAndCategoryAndMonthAndYear(
            User user,
            String category,
            Integer month,
            Integer year
    );
}