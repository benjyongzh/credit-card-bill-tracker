package com.credit_card_bill_tracker.backend.creditcard;

import com.credit_card_bill_tracker.backend.common.BaseEntity;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "credit_cards")
@Getter
@Setter
@NoArgsConstructor
public class CreditCard extends BaseEntity {

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String cardName;

    @Column
    private String lastFourDigits;
}