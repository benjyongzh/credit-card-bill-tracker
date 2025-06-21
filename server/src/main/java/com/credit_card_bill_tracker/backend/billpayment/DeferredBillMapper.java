package com.credit_card_bill_tracker.backend.billpayment;

//import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRepository;
//import com.credit_card_bill_tracker.backend.creditcard.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeferredBillMapper {

//    private final CreditCardRepository creditCardRepository;
//    private final BankAccountRepository bankAccountRepository;

//    public BillPaymentDTO toDto(BillPayment entity) {
//        BillPaymentDTO dto = new BillPaymentDTO();
//        dto.setAmount(entity.getAmount());
//        dto.setDate(entity.getDate());
//        dto.setCompleted(entity.isCompleted());
//        dto.setFromAccountId(entity.getFromAccount().getId());
//        if (entity.getToCard() != null) dto.setToCardId(entity.getToCard().getId());
//        if (entity.getToAccount() != null) dto.setToAccountId(entity.getToAccount().getId());
//        return dto;
//    }

    public DeferredBillResponseDTO toResponseDto(DeferredBill entity) {
        DeferredBillResponseDTO dto = new DeferredBillResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setFromAccountId(entity.getFromAccount().getId());
        if (entity.getToCard() != null) dto.setToCardId(entity.getToCard().getId());
        if (entity.getToAccount() != null) dto.setToAccountId(entity.getToAccount().getId());
        dto.setAmount(entity.getAmount());
        dto.setFromAccountId(entity.getFromAccount().getId());
        dto.setBillingCycleId(entity.getBillingCycle().getId());
        return dto;
    }

//    public BillPayment fromDto(BillPaymentDTO dto) {
//        BillPayment entity = new BillPayment();
//        entity.setAmount(dto.getAmount());
//        entity.setDate(dto.getDate());
//        entity.setCompleted(dto.isCompleted());
//        entity.setFromAccount(bankAccountRepository.getReferenceById(dto.getFromAccountId()));
//        if (dto.getToCardId() != null) entity.setToCard(creditCardRepository.getReferenceById(dto.getToCardId()));
//        if (dto.getToAccountId() != null) entity.setToAccount(bankAccountRepository.getReferenceById(dto.getToAccountId()));
//        return entity;
//    }

//    public void updateEntityFromDto(BillPayment entity, BillPaymentDTO dto) {
//        entity.setAmount(dto.getAmount());
//        entity.setDate(dto.getDate());
//        entity.setFromAccount(bankAccountRepository.getReferenceById(dto.getFromAccountId()));
//        entity.setToCard(dto.getToCardId() != null ? creditCardRepository.getReferenceById(dto.getToCardId()) : null);
//        entity.setToAccount(dto.getToAccountId() != null ? bankAccountRepository.getReferenceById(dto.getToAccountId()) : null);
//    }
}