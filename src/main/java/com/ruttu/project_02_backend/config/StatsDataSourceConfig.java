package com.ruttu.project_02_backend.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.ruttu.project_02_backend.repository.stats",
        entityManagerFactoryRef = "statsEntityManagerFactory",
        transactionManagerRef = "statsTransactionManager"
)
public class StatsDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.stats")
    public DataSource statsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean statsEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("statsDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.ruttu.project_02_backend.entity.stats")
                .persistenceUnit("stats")
                .properties(Map.of(
                        "hibernate.dialect", "org.hibernate.dialect.MySQLDialect",
                        "hibernate.hbm2ddl.auto", "update"
                ))
                .build();
    }

    @Bean
    public PlatformTransactionManager statsTransactionManager(
            @Qualifier("statsEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}