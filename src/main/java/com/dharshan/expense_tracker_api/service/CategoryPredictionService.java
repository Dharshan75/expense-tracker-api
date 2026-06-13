package com.dharshan.expense_tracker_api.service;

import org.springframework.stereotype.Service;

@Service
public class CategoryPredictionService {

    public String predictCategory(String merchantName) {

        if (merchantName == null) {
            return "Others";
        }

        String merchant = merchantName.toLowerCase();

        // Food
        if (merchant.contains("swiggy")
                || merchant.contains("zomato")
                || merchant.contains("nescafe")
                || merchant.contains("dominos")
                || merchant.contains("pizza")) {

            return "Food";
        }

        // Transport
        if (merchant.contains("uber")
                || merchant.contains("ola")
                || merchant.contains("rapido")) {

            return "Transport";
        }

        // Shopping
        if (merchant.contains("amazon")
                || merchant.contains("flipkart")
                || merchant.contains("myntra")) {

            return "Shopping";
        }

        // Entertainment
        if (merchant.contains("netflix")
                || merchant.contains("spotify")
                || merchant.contains("hotstar")) {

            return "Entertainment";
        }

        // Healthcare
        if (merchant.contains("apollo")
                || merchant.contains("hospital")
                || merchant.contains("clinic")) {

            return "Healthcare";
        }

        return "Others";
    }
}