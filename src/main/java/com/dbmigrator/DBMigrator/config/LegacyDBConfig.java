package com.dbmigrator.DBMigrator.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoRepositories(
        basePackages = {"com.dbmigrator.DBMigrator.domain.legacy"} // MongoDB가 매핑할 Entity 가 있는 패키지 위치
)
@ComponentScan(basePackages = {"com.dbmigrator.DBMigrator.domain.legacy"})
public class LegacyDBConfig {
    @Value("${spring.mongodb.host}")
    private String legacyDBHost;

    @Value("${spring.mongodb.port}")
    private String legacyDBPort;

    @Value("${spring.mongodb.base-package}")
    private String legacyDBBasePackage;

    @Value("${spring.mongodb.username}")
    private String legacyDBUsername;

    @Value("${spring.mongodb.password}")
    private String legacyDBPassword;

    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings.Builder clientSettingsBuilder = MongoClientSettings.builder()
                .applyToSocketSettings(builder -> {
                    // Timeout Configurations
                    builder.connectTimeout(1000, TimeUnit.MILLISECONDS);
                    builder.readTimeout(1000, TimeUnit.MILLISECONDS);
                })
                .applyConnectionString(new ConnectionString("mongodb://" + legacyDBUsername + ":" + legacyDBPassword + "@" + legacyDBHost + ":" + legacyDBPort + "/?authSource=admin"));

        return MongoClients.create(clientSettingsBuilder.build());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), legacyDBBasePackage);
    }

}
