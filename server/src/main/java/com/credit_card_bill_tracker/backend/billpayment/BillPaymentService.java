package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccountRepository;
import com.credit_card_bill_tracker.backend.creditcard.CreditCardRepository;
import com.credit_card_bill_tracker.backend.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillPaymentService {

    private final BillPaymentRepository billPaymentRepo;
    private final BankAccountRepository bankAccountRepo;
    private final CreditCardRepository creditCardRepo;

    public List<BillPayment> getAllByUser(UUID userId) {
        return billPaymentRepo.findByUserIdAndDeletedFalse(userId);
    }

    @Transactional
    public BillPayment create(User user, BillPaymentDTO dto) {
        BillPayment payment = new BillPayment();
        payment.setUser(user);
        payment.setFromAccount(bankAccountRepo.findById(dto.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("From account not found")));
        if (dto.getToCardId() != null) {
            payment.setToCard(creditCardRepo.findById(dto.getToCardId())
                    .orElseThrow(() -> new RuntimeException("Card not found")));
        }
        if (dto.getToAccountId() != null) {
            payment.setToAccount(bankAccountRepo.findById(dto.getToAccountId())
                    .orElseThrow(() -> new RuntimeException("To account not found")));
        }
        payment.setAmount(dto.getAmount());
        payment.setDate(dto.getDate());

        return billPaymentRepo.save(payment);
    }

    public void delete(UUID id) {
        BillPayment payment = billPaymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setDeleted(true);
        billPaymentRepo.save(payment);
    }
}
