package com.credit_card_bill_tracker.backend.config;

import com.credit_card_bill_tracker.backend.common.BaseRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
        basePackages = "com.credit_card_bill_tracker.backend", // this is your root
        repositoryBaseClass = BaseRepositoryImpl.class
)
public class JpaConfig {
}
