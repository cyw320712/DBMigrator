package com.dbmigrater.DBMigrater.domain.migration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationUserRepository extends JpaRepository<NewUser, Long> {
    NewUser findUserByUserId(Long userId);
}
