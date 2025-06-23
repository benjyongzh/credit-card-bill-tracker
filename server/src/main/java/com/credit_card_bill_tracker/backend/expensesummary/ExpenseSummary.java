package com.credit_card_bill_tracker.backend.expensesummary;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.common.BaseEntity;
import com.credit_card_bill_tracker.backend.common.errors.BadRequestException;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "expense_summaries", uniqueConstraints = @jakarta.persistence.UniqueConstraint(columnNames = {"user_id", "from_account_id", "to_card_id", "to_account_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ExpenseSummary extends BaseEntity {

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private BankAccount fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_card_id")
    private CreditCard toCard;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
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

    @PrePersist
    @PreUpdate
    private void validateToFields() {
        if ((toCard == null && toAccount == null) || (toCard != null && toAccount != null)) {
            throw new BadRequestException("Exactly one of toCard or toAccount must be set.", List.of("Expense Summary: " + this.getId()));
        }
    }
}