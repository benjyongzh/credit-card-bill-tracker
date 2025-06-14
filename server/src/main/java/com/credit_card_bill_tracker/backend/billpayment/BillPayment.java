package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.common.BaseEntity;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "bill_payments")
@Getter
@Setter
@NoArgsConstructor
public class BillPayment extends BaseEntity {

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

    @Column(nullable = false)
    private LocalDate date;
}
