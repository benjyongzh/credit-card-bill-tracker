package com.credit_card_bill_tracker.backend.common;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
}
