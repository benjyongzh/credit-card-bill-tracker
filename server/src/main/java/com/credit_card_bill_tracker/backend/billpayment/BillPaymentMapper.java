package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRepository;
import com.credit_card_bill_tracker.backend.creditcard.CreditCardRepository;
import com.credit_card_bill_tracker.backend.billpayment.BillPaymentRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillPaymentMapper {

    private final CreditCardRepository creditCardRepository;
    private final BankAccountRepository bankAccountRepository;

    public BillPaymentRequestDTO toDto(BillPayment entity) {
        BillPaymentRequestDTO dto = new BillPaymentRequestDTO();
        dto.setAmount(entity.getAmount());
        dto.setDate(entity.getDate());
        dto.setCompleted(entity.isCompleted());
        dto.setFromAccountId(entity.getFromAccount().getId());
        if (entity.getToCard() != null) dto.setToCardId(entity.getToCard().getId());
        if (entity.getToAccount() != null) dto.setToAccountId(entity.getToAccount().getId());
        return dto;
    }

    public BillPaymentResponseDTO toResponseDto(BillPayment entity) {
        BillPaymentResponseDTO dto = new BillPaymentResponseDTO();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setDate(entity.getDate());
        dto.setCompleted(entity.isCompleted());
        dto.setFromAccountId(entity.getFromAccount().getId());
        if (entity.getToCard() != null) dto.setToCardId(entity.getToCard().getId());
        if (entity.getToAccount() != null) dto.setToAccountId(entity.getToAccount().getId());
        return dto;
    }

    public BillPayment fromDto(BillPaymentRequestDTO dto) {
        BillPayment entity = new BillPayment();
        entity.setAmount(dto.getAmount());
        entity.setDate(dto.getDate());
        entity.setCompleted(dto.isCompleted());
        entity.setFromAccount(bankAccountRepository.getReferenceById(dto.getFromAccountId()));
        if (dto.getToCardId() != null) entity.setToCard(creditCardRepository.getReferenceById(dto.getToCardId()));
        if (dto.getToAccountId() != null) entity.setToAccount(bankAccountRepository.getReferenceById(dto.getToAccountId()));
        return entity;
    }

    public void updateEntityFromDto(BillPayment entity, BillPaymentRequestDTO dto) {
        entity.setAmount(dto.getAmount());
        entity.setDate(dto.getDate());
        entity.setFromAccount(bankAccountRepository.getReferenceById(dto.getFromAccountId()));
        entity.setToCard(dto.getToCardId() != null ? creditCardRepository.getReferenceById(dto.getToCardId()) : null);
        entity.setToAccount(dto.getToAccountId() != null ? bankAccountRepository.getReferenceById(dto.getToAccountId()) : null);
    }
}