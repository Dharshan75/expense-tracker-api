package com.dharshan.expense_tracker_api.service;

import com.dharshan.expense_tracker_api.dto.RecurringExpenseRequest;
import com.dharshan.expense_tracker_api.model.RecurringExpense;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.repository.RecurringExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class RecurringExpenseService {

    private final RecurringExpenseRepository recurringExpenseRepository;

    // ===============================
    // CREATE RECURRING EXPENSE
    // ===============================
    public RecurringExpense createRecurringExpense(
            RecurringExpenseRequest request,
            User user) {

        RecurringExpense recurringExpense =
                RecurringExpense.builder()

                        .title(request.getTitle())
                        .category(request.getCategory())
                        .amount(request.getAmount())
                        .frequency(request.getFrequency())
                        .user(user)

                        .build();

        return recurringExpenseRepository.save(
                recurringExpense
        );
    }

    // ===============================
    // GET ALL RECURRING EXPENSES
    // ===============================
    public List<RecurringExpense> getRecurringExpenses(
            User user) {

        return recurringExpenseRepository.findByUser(user);

    }
    // ===============================
// DELETE RECURRING EXPENSE
// ===============================
    @Transactional
    public void deleteRecurringExpense(UUID id, User user) {

        recurringExpenseRepository
                .deleteByIdAndUser(id, user);

    }
    @Transactional
    public RecurringExpense updateRecurringExpense(

            UUID id,
            RecurringExpenseRequest request,
            User user) {

        RecurringExpense expense =
                recurringExpenseRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Recurring expense not found"
                                )
                        );

        expense.setTitle(request.getTitle());
        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setFrequency(request.getFrequency());

        return recurringExpenseRepository.save(expense);

    }

}