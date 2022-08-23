package com.dbmigrater.DBMigrater.domain.legacy;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface LegacyUserRepository extends MongoRepository<LegacyUser, Long> {
}
