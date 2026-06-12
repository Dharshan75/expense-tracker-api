package com.dharshan.expense_tracker_api.controller;

import com.dharshan.expense_tracker_api.dto.SmsParseRequest;
import com.dharshan.expense_tracker_api.dto.SmsParseResponse;
import com.dharshan.expense_tracker_api.service.SmsParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsParserService smsParserService;

    // ===============================
    // PARSE SMS
    // ===============================
    @PostMapping("/parse")
    public SmsParseResponse parseSms(
            @RequestBody SmsParseRequest request) {

        return smsParserService.parse(
                request.getMessage()
        );
    }
}