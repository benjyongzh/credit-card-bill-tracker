package com.credit_card_bill_tracker.backend.expensesummary;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
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

import java.util.UUID;

@Entity
@Table(name = "expense_summaries")
@Getter
@Setter
@NoArgsConstructor
public class ExpenseSummary extends BaseEntity {

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private BankAccount fromAccount;

    @ManyToOne
    private CreditCard toCard;

    @ManyToOne
    private BankAccount toAccount;

    public String getToType() {
        return toCard != null ? "card" : "account";
    }

    public UUID getToId() {
        return toCard != null ? toCard.getId() : (toAccount != null ? toAccount.getId() : null);
    }

    @Column(nullable = false)
    private double totalExpense = 0.0;

    @Column(nullable = false)
    private double totalPaid = 0.0;

    public double getRemaining() {
        return totalExpense - totalPaid;
    }

    public void updateExpense(double amount, boolean isAdding) {
        if (isAdding) {
            this.totalExpense += amount;
        } else {
            this.totalExpense -= amount;
        }
    }

    public void updatePayment(double amount, boolean isAdding) {
        if (isAdding) {
            this.totalPaid += amount;
        } else {
            this.totalPaid -= amount;
        }
    }
}