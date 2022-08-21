package com.dbmigrater.DBMigrater.repository.migration;

import com.dbmigrater.DBMigrater.entity.migration.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationUserRepository extends JpaRepository<User, Integer> {
}
