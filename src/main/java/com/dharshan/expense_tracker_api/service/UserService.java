package com.dharshan.expense_tracker_api.service;

import com.dharshan.expense_tracker_api.model.Subscription;
import com.dharshan.expense_tracker_api.model.SubscriptionType;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.dharshan.expense_tracker_api.repository.SubscriptionRepository;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionRepository subscriptionRepository;

    // ===============================
    // REGISTER USER
    // ===============================
    public User registerUser(User user) {

        String email = user.getEmail().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user first
        User savedUser = userRepository.save(user);

        // Create FREE subscription automatically
        Subscription subscription = Subscription.builder()
                .type(SubscriptionType.FREE)
                .startDate(LocalDate.now())
                .endDate(null)          // FREE plan never expires
                .autoRenew(false)
                .active(true)
                .user(savedUser)
                .build();

        subscriptionRepository.save(subscription);

        return savedUser;
    }

    // ===============================
    // LOGIN USER
    // ===============================
    public User loginUser(String email, String password) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    // ===============================
    // GET USER BY EMAIL
    // ===============================
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public void changePassword(
            String email,
            String currentPassword,
            String newPassword) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {

            throw new RuntimeException(
                    "Current password is incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }
}