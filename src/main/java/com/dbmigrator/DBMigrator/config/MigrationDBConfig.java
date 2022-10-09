package com.dbmigrator.DBMigrator.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration // Spring Configuration 임을 명시하는 annotation
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = { "com.dbmigrator.DBMigrator.domain.migration" }// Postgres 가 매핑할 Entity 가 있는 패키지 위치
)
@ComponentScan(basePackages = {"com.dbmigrator.DBMigrator.domain.migration"})
public class MigrationDBConfig {
    @Value("${spring.postgres.host}")
    private String migrationDBHost;

    @Value("${spring.postgres.port}")
    private String migrationDBPort;

    @Value("${spring.postgres.dbname}")
    private String migrationDBName;

    @Value("${spring.postgres.dbschema}")
    private String migrationDBSchema;

    @Value("${spring.postgres.username}")
    private String migrationDBUsername;

    @Value("${spring.postgres.password}")
    private String migrationDBPassword;

    @Value("${spring.jpa.hibernate.}")

    @Bean(name = "migrationDataSource")
    public HikariDataSource migrationDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setJdbcUrl("jdbc:postgresql://" + migrationDBHost + ":" + migrationDBPort + "/" + migrationDBName + "?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true");
        hikariConfig.setUsername(migrationDBUsername);
        hikariConfig.setPassword(migrationDBPassword);
        hikariConfig.setSchema(migrationDBSchema);

        return new HikariDataSource(hikariConfig);
    }
}