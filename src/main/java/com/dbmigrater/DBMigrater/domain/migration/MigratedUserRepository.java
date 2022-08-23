package com.dbmigrater.DBMigrater.domain.migration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MigratedUserRepository extends JpaRepository<MigratedUser, Long> {
    MigratedUser findUserByUserId(Long userId);
}
