package com.ruttu.project_02_backend.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.ruttu.project_02_backend.repository.prod",
        entityManagerFactoryRef = "prodEntityManagerFactory",
        transactionManagerRef = "prodTransactionManager"
)
public class ProdDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.prod")
    public DataSource prodDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean prodEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("prodDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(
                        "com.ruttu.project_02_backend.entity.prod.routine",
                        "com.ruttu.project_02_backend.entity.prod.auth",
                        "com.ruttu.project_02_backend.entity.prod.report",
                        "com.ruttu.project_02_backend.entity.prod.user",
                        "com.ruttu.project_02_backend.entity.prod.feed"
                )
                .persistenceUnit("prod")
                .properties(Map.of(
                        "hibernate.dialect", "org.hibernate.dialect.MySQLDialect",
                        "hibernate.hbm2ddl.auto", "update"
                ))
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager prodTransactionManager(
            @Qualifier("prodEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}