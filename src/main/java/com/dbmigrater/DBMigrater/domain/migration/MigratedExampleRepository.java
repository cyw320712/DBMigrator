package com.dbmigrater.DBMigrater.domain.migration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MigratedExampleRepository extends JpaRepository<MigratedExample, Long> {
}
