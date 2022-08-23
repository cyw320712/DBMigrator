package com.dbmigrater.DBMigrater.domain.migration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationUserRepository extends JpaRepository<User, Long> {
    User findUserByUserId(Long userId);
}
