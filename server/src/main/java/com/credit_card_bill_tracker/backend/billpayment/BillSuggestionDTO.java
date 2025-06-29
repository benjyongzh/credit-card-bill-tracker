package com.credit_card_bill_tracker.backend.billpayment;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.credit_card_bill_tracker.backend.expensesummary.TargetType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillSuggestionDTO {
    private UUID from;
    private UUID to;
    private double amount;
    private TargetType toType;
}
