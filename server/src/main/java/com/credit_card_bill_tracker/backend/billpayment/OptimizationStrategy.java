package com.credit_card_bill_tracker.backend.billpayment;

public enum OptimizationStrategy {
    MINIMIZE_TRANSACTIONS,
    MINIMIZE_ACCOUNTS;

    public static final String DEFAULT = "MINIMIZE_TRANSACTIONS";
}
