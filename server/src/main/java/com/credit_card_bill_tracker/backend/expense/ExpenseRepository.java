package com.credit_card_bill_tracker.backend.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByUserIdAndDeletedFalse(UUID userId);
    List<Expense> findByUserIdAndCreditCardIdAndDeletedFalse(UUID userId, UUID cardId);

    @Query("SELECT e FROM Expense e WHERE e.deleted = false " +
            "AND e.user.id = :userId AND e.id NOT IN " +
            "(SELECT bp.id FROM BillPayment bp WHERE bp.user.id = :userId AND bp.deleted = false)")
    List<Expense> findUnpaidExpenses(@Param("userId") UUID userId);
}