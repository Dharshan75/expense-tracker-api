package com.dharshan.expense_tracker_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private SubscriptionType type;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean autoRenew;

    private Boolean active;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}