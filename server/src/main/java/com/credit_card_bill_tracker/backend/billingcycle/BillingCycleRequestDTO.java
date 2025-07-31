package com.credit_card_bill_tracker.backend.billingcycle;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Month;

@Data
public class BillingCycleRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String label;

    @NotNull
    private Month month;
}