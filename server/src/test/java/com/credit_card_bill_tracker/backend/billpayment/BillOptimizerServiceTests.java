package com.credit_card_bill_tracker.backend.billpayment;

import com.credit_card_bill_tracker.backend.bankaccount.BankAccount;
import com.credit_card_bill_tracker.backend.creditcard.CreditCard;
import com.credit_card_bill_tracker.backend.expensesummary.ExpenseSummary;
import com.credit_card_bill_tracker.backend.expensesummary.ExpenseSummaryRepository;
import com.credit_card_bill_tracker.backend.expensesummary.TargetType;
import com.credit_card_bill_tracker.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BillOptimizerServiceTests {

    private ExpenseSummaryRepository repository;
    private BillOptimizerService service;

    @BeforeEach
    void setUp() {
        repository = mock(ExpenseSummaryRepository.class);
        service = new BillOptimizerService(repository);
    }

    private ExpenseSummary buildSummary(User user, BankAccount from, UUID toId, TargetType toType, double amount) {
        ExpenseSummary s = new ExpenseSummary();
        s.setUser(user);
        s.setFromAccount(from);
        s.setToId(toId);
        s.setToType(toType);
        s.setTotalExpense(amount);
        s.setTotalPaid(0);
        return s;
    }

    @Test
    void singleAccountSingleCard() {
        User user = new User();
        user.setId(UUID.randomUUID());

        BankAccount bank = new BankAccount();
        bank.setId(UUID.randomUUID());
        CreditCard card = new CreditCard();
        card.setId(UUID.randomUUID());

        ExpenseSummary summary = buildSummary(user, bank, card.getId(), TargetType.CARD, 100);

        when(repository.findByUserId(user.getId())).thenReturn(List.of(summary));

        List<BillSuggestionDTO> result = service.computeBillSuggestions(user);

        assertEquals(1, result.size());
        BillSuggestionDTO dto = result.get(0);
        assertEquals(bank.getId(), dto.getFrom());
        assertEquals(card.getId(), dto.getTo());
        assertEquals(100, dto.getAmount());
        assertEquals(TargetType.CARD, dto.getToType());
    }

    @Test
    void indirectPaymentsAreOptimized() {
        User user = new User();
        user.setId(UUID.randomUUID());

        BankAccount a = new BankAccount();
        a.setId(UUID.randomUUID());
        BankAccount b = new BankAccount();
        b.setId(UUID.randomUUID());
        CreditCard card = new CreditCard();
        card.setId(UUID.randomUUID());

        ExpenseSummary s1 = buildSummary(user, a, b.getId(), TargetType.ACCOUNT, 50);
        ExpenseSummary s2 = buildSummary(user, b, card.getId(), TargetType.CARD, 100);

        when(repository.findByUserId(user.getId())).thenReturn(List.of(s1, s2));

        List<BillSuggestionDTO> result = service.computeBillSuggestions(user);

        assertEquals(2, result.size());
        // total amount to card should equal 100
        double totalToCard = result.stream()
                .filter(r -> r.getTo().equals(card.getId()))
                .mapToDouble(BillSuggestionDTO::getAmount)
                .sum();
        assertEquals(100, totalToCard);
    }

    @Test
    void overpaymentProducesNoSuggestions() {
        User user = new User();
        user.setId(UUID.randomUUID());

        BankAccount bank = new BankAccount();
        bank.setId(UUID.randomUUID());
        CreditCard card = new CreditCard();
        card.setId(UUID.randomUUID());

        ExpenseSummary summary = buildSummary(user, bank, card.getId(), TargetType.CARD, -50);

        when(repository.findByUserId(user.getId())).thenReturn(List.of(summary));

        List<BillSuggestionDTO> result = service.computeBillSuggestions(user);

        assertEquals(0, result.size());
    }

    @Test
    void overpaymentDoesNotReduceOthersBills() {
        User user = new User();
        user.setId(UUID.randomUUID());

        BankAccount a = new BankAccount();
        a.setId(UUID.randomUUID());
        BankAccount b = new BankAccount();
        b.setId(UUID.randomUUID());
        CreditCard card = new CreditCard();
        card.setId(UUID.randomUUID());

        ExpenseSummary overpay = buildSummary(user, a, card.getId(), TargetType.CARD, -20);
        ExpenseSummary due = buildSummary(user, b, card.getId(), TargetType.CARD, 30);

        when(repository.findByUserId(user.getId())).thenReturn(List.of(overpay, due));

        List<BillSuggestionDTO> result = service.computeBillSuggestions(user);

        assertEquals(1, result.size());
        BillSuggestionDTO dto = result.get(0);
        assertEquals(b.getId(), dto.getFrom());
        assertEquals(card.getId(), dto.getTo());
        assertEquals(30, dto.getAmount());
    }
}
