package com.credit_card_bill_tracker.backend.expensesummary;

import com.credit_card_bill_tracker.backend.common.errors.UnauthorizedException;
import com.credit_card_bill_tracker.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseSummaryServiceTests {

    private ExpenseSummaryRepository repository;
    private ExpenseSummaryMapper mapper;
    private ExpenseSummaryService service;

    @BeforeEach
    void setUp() {
        repository = mock(ExpenseSummaryRepository.class);
        mapper = mock(ExpenseSummaryMapper.class);
        service = new ExpenseSummaryService(repository, mapper);
    }

    @Test
    void userCannotAccessOtherUsersSummaries() {
        User user = new User();
        user.setId(UUID.randomUUID());
        UUID otherId = UUID.randomUUID();

        assertThrows(UnauthorizedException.class, () -> service.getAllSummaries(user, otherId));
        verify(repository, never()).findByUserId(any());
    }

    @Test
    void returnsMappedSummariesForUser() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);

        ExpenseSummary summary = new ExpenseSummary();
        ExpenseSummaryResponseDTO dto = new ExpenseSummaryResponseDTO();

        when(repository.findByUserId(id)).thenReturn(List.of(summary));
        when(mapper.toResponseDto(summary)).thenReturn(dto);

        List<ExpenseSummaryResponseDTO> result = service.getAllSummaries(user, id);

        assertEquals(List.of(dto), result);
        verify(repository).findByUserId(id);
    }
}
