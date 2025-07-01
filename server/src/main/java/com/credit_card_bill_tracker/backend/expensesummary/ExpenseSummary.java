package com.credit_card_bill_tracker.backend.expensesummary;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.common.BaseEntity;
import com.credit_card_bill_tracker.backend.common.errors.BadRequestException;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "expense_summaries", uniqueConstraints = @jakarta.persistence.UniqueConstraint(columnNames = {"user_id", "from_account_id", "to_id", "to_type"}))
@Getter
@Setter
@NoArgsConstructor
public class ExpenseSummary extends BaseEntity {

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private BankAccount fromAccount;

    @Column(name = "to_id", nullable = false)
    private UUID toId;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_type", nullable = false)
    private TargetType toType;

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
        if (toType == null) {
            throw new BadRequestException("toType cannot be null", List.of("Expense Summary: " + this.getId()));
        }
        if (toId == null) {
            throw new BadRequestException("toId cannot be null", List.of("Expense Summary: " + this.getId()));
        }
    }
}