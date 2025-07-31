package com.credit_card_bill_tracker.backend.spendingprofile;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.common.BaseEntity;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(
        name = "spending_profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_profile_user_name", columnNames = {"user_id", "name"})
)
@Getter
@Setter
@NoArgsConstructor
public class SpendingProfile extends BaseEntity {

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "spending_profile_bank_accounts",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "bank_account_id")
    )
    private List<BankAccount> bankAccounts;
}