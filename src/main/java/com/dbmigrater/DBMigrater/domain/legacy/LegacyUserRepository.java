package com.dbmigrater.DBMigrater.domain.legacy;

import com.dbmigrater.DBMigrater.domain.legacy.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface LegacyUserRepository extends MongoRepository<User, Long> {
    User findUserByUserId(Long userId);

    List<User> findUserByUserIdBetween(Long startId, Long endId);
}
