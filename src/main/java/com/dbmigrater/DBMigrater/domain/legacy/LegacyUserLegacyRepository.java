package com.dbmigrater.DBMigrater.domain.legacy;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface LegacyUserLegacyRepository extends MongoRepository<LegacyUser, Long> {
}
