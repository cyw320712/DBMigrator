package com.dbmigrator.DBMigrator.repository;

import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;

public interface BaseMigrationRepository<T extends BaseMigrationEntity, Long> extends BaseJpaRepository<T, Long>{
}
