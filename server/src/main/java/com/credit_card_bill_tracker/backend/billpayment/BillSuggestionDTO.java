package com.credit_card_bill_tracker.backend.billpayment;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillSuggestionDTO {
    private UUID from;
    private UUID to;
    private double amount;
    private String toType; //"card" or "account"
}
