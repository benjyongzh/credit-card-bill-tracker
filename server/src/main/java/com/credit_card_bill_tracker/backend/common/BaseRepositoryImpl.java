package com.credit_card_bill_tracker.backend.common;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BaseRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                              EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<T> findAll() {
        enableSoftDeleteFilter();
        return super.findAll();
    }

    @Override
    public Optional<T> findById(ID id) {
        enableSoftDeleteFilter();
        return super.findById(id);
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        enableSoftDeleteFilter();
        return super.findAllById(ids);
    }

    // Add other overrides as needed (e.g., findAll(Specification), Page, etc.)

    private void enableSoftDeleteFilter() {
        var session = entityManager.unwrap(org.hibernate.Session.class);
        var filter = session.getEnabledFilter("softDeleteFilter");
        if (filter == null) {
            session.enableFilter("softDeleteFilter")
                    .setParameter("now", LocalDateTime.now());
        }
    }
}
