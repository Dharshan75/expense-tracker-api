package com.dharshan.expense_tracker_api.service;

import com.dharshan.expense_tracker_api.dto.SmsParseResponse;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmsParserService {

    public SmsParseResponse parse(String message) {

        String lower = message.toLowerCase();

        boolean isIncome =
                lower.contains("credited");

        boolean isExpense =
                lower.contains("debited")
                        || lower.contains("spent")
                        || lower.contains("paid")
                        || lower.contains("sent");

        if (!isExpense && !isIncome) {

            return SmsParseResponse.builder()
                    .expense(false)
                    .income(false)
                    .rawMessage(message)
                    .build();
        }

        return SmsParseResponse.builder()
                .expense(isExpense && !isIncome)
                .income(isIncome)
                .amount(extractAmount(message))
                .merchantName(extractMerchant(message))
                .transactionId(extractTransactionId(message))
                .bankName(detectBank(message))
                .rawMessage(message)
                .build();
    }

    private Double extractAmount(String message) {

        // Rs.50.00
        Pattern rsPattern =
                Pattern.compile("Rs[:.]?\\s*(\\d+(?:\\.\\d+)?)",
                        Pattern.CASE_INSENSITIVE);

        Matcher rsMatcher = rsPattern.matcher(message);

        if (rsMatcher.find()) {
            return Double.parseDouble(rsMatcher.group(1));
        }

        // INR 500
        Pattern inrPattern =
                Pattern.compile("INR\\s*(\\d+(?:\\.\\d+)?)",
                        Pattern.CASE_INSENSITIVE);

        Matcher inrMatcher = inrPattern.matcher(message);

        if (inrMatcher.find()) {
            return Double.parseDouble(inrMatcher.group(1));
        }

        // debited by 50.00
        Pattern debitPattern =
                Pattern.compile("debited\\s+by\\s+(\\d+(?:\\.\\d+)?)",
                        Pattern.CASE_INSENSITIVE);

        Matcher debitMatcher = debitPattern.matcher(message);

        if (debitMatcher.find()) {
            return Double.parseDouble(debitMatcher.group(1));
        }

        return 0.0;
    }

    private String extractMerchant(String message) {

        // SBI
        Pattern sbi =
                Pattern.compile("trf to\\s(.+?)\\sRefno",
                        Pattern.CASE_INSENSITIVE);

        Matcher sbiMatcher = sbi.matcher(message);

        if (sbiMatcher.find()) {
            return sbiMatcher.group(1).trim();
        }

        // HDFC / Indian Bank / Canara
        Pattern toPattern =
                Pattern.compile("to\\s(.+?)(?:,|\\.|UPI Ref|Ref)",
                        Pattern.CASE_INSENSITIVE);

        Matcher toMatcher = toPattern.matcher(message);

        if (toMatcher.find()) {
            return toMatcher.group(1).trim();
        }

        // IOB
        Pattern iobPattern =
                Pattern.compile("payee\\s(.+?)\\sfor\\sRs",
                        Pattern.CASE_INSENSITIVE);

        Matcher iobMatcher = iobPattern.matcher(message);

        if (iobMatcher.find()) {
            return iobMatcher.group(1).trim();
        }

        // Union Bank
        Pattern unionPattern =
                Pattern.compile("Fvg:\\s(.+?)\\sAvl",
                        Pattern.CASE_INSENSITIVE);

        Matcher unionMatcher = unionPattern.matcher(message);

        if (unionMatcher.find()) {
            return unionMatcher.group(1).trim();
        }

        if (message.toLowerCase().contains("mobikwik")) {
            return "MobiKwik";
        }

        return "Unknown";
    }

    private String extractTransactionId(String message) {

        Pattern pattern =
                Pattern.compile(
                        "(?:Ref\\s?No\\.?|Refno|ref no|UPI Ref|Ref)\\s?(\\d+)",
                        Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private String detectBank(String message) {

        String lower = message.toLowerCase();

        if (lower.contains("hdfc"))
            return "HDFC";

        if (lower.contains("sbi"))
            return "SBI";

        if (lower.contains("canara"))
            return "Canara Bank";

        if (lower.contains("union bank"))
            return "Union Bank";

        if (lower.contains("indian bank"))
            return "Indian Bank";

        if (lower.contains("iob"))
            return "IOB";

        if (lower.contains("mobikwik"))
            return "MobiKwik";

        return "Unknown";
    }
}