package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bills/optimizer")
@RequiredArgsConstructor
public class BillOptimizerController {

    private final BillOptimizerService service;

    @GetMapping
    public Map<String, Double> getOptimized(@AuthenticationPrincipal User user) {
        return service.computeBills(user);
    }
}
