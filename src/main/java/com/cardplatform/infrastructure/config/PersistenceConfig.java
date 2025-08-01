package com.cardplatform.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration class for persistence layer.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.cardplatform.infrastructure.persistence.repository")
@EnableTransactionManagement
public class PersistenceConfig {
}
