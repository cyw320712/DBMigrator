package com.dbmigrator.DBMigrator.config;

import com.mongodb.ConnectionString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

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

    private final MongoMappingContext mongoMappingContext;

    public LegacyDBConfig(MongoMappingContext mongoMappingContext){
        this.mongoMappingContext = mongoMappingContext;
    }

    @Bean
    public MongoDatabaseFactory mongoDBFactory() {
        String connectionString = "mongodb://" + legacyDBUsername + ":" + legacyDBPassword + "@" + legacyDBHost + ":" + legacyDBPort + "/" + legacyDBBasePackage + "?authSource=admin";
        return new SimpleMongoClientDatabaseFactory(new ConnectionString(connectionString));
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDBFactory());
        MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        mongoConverter.setTypeMapper(new DefaultMongoTypeMapper());

        return new MongoTemplate(mongoDBFactory(), mongoConverter);
    }
}
