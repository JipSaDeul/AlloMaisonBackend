package com.example.allomaison.Config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MaisonDbConfig {

    @Primary // This is the main DataSource
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties maisonDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource maisonDataSource() {
        return maisonDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("maisonDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}

