package com.dbmigrater.DBMigrater.domain.legacy;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface LegacyExampleRepository extends MongoRepository<LegacyExample, Long> {
}
