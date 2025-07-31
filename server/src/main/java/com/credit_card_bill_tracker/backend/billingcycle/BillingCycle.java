package com.credit_card_bill_tracker.backend.billingcycle;

import com.credit_card_bill_tracker.backend.billpayment.BillPayment;
import com.credit_card_bill_tracker.backend.common.BaseEntity;
import com.credit_card_bill_tracker.backend.expense.Expense;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name = "billing_cycles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cycle_user_label", columnNames = {"user_id", "label"}),
                @UniqueConstraint(name = "uk_cycle_user_month", columnNames = {"user_id", "month"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class BillingCycle extends BaseEntity {

    @ManyToOne(optional = false)
    private User user;

    @OneToMany(mappedBy = "billingCycle")
    private List<BillPayment> billPayments;

    @OneToMany(mappedBy = "billingCycle")
    private List<Expense> expenses;

    @Column(nullable = false)
    private String label; // e.g. "June 2025"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private java.time.Month month;

    private LocalDate completedDate;
}