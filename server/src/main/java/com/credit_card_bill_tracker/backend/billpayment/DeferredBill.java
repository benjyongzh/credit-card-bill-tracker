package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.billingcycle.BillingCycle;
import com.credit_card_bill_tracker.backend.common.BaseEntity;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deferred_bill")
@Getter
@Setter
@NoArgsConstructor
public class DeferredBill extends BaseEntity {

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private BankAccount fromAccount;

    @ManyToOne
    private CreditCard toCard;

    @ManyToOne
    private BankAccount toAccount;

    @Column(nullable = false)
    private double amount;

    @ManyToOne(optional = false)
    private BillingCycle billingCycle;
}
