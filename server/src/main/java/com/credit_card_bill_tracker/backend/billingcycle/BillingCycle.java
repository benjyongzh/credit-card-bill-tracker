package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.billpayment.BillPayment;
import com.credit_card_bill_tracker.backend.common.BaseEntity;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "billing_cycles")
@Getter
@Setter
@NoArgsConstructor
public class BillingCycle extends BaseEntity {

    @ManyToOne(optional = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "billing_cycle_payments",
            joinColumns = @JoinColumn(name = "billing_cycle_id"),
            inverseJoinColumns = @JoinColumn(name = "bill_payment_id")
    )
    private List<BillPayment> billPayments;

    @Column(nullable = false)
    private String label; // e.g. "June 2025"

    @Column(nullable = false)
    private LocalDate completedDate;
}