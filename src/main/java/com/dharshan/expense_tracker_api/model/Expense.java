package com.dharshan.expense_tracker_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Double amount;

    private String category;

    private String description;

    private LocalDate date;

    private String suggestedCategory;

    // MANUAL, SMS, IMPORT, AI
    @Enumerated(EnumType.STRING)
    private ExpenseSource source;

    // Example: Swiggy, Amazon, Uber
    private String merchantName;

    // Unique bank transaction ID (if available)
    @Column(unique = true)
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ExpenseStatus status;
}