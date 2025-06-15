package com.credit_card_bill_tracker.backend.bankaccount;

import com.credit_card_bill_tracker.backend.common.BaseEntity;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
@NoArgsConstructor
public class BankAccount extends BaseEntity {

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String name; // e.g. DBS Multiplier

    @Column(nullable = false)
    private boolean isDefault = false;

    @ManyToOne
    @JoinColumn(name = "default_card_id")
    private CreditCard defaultCard;

    public void setIsDefault(boolean bool) {
        this.isDefault = bool;
    }
}