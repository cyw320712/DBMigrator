package com.dbmigrator.DBMigrator.domain.migration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MigrationExampleRepository extends JpaRepository<MigrationExample, Long> {
}
