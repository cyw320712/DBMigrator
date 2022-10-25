package com.dbmigrator.DBMigrator.repository;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;

public interface BaseLegacyRepository<T extends BaseLegacyEntity, String> extends BaseMongoRepository<T, String>{
}
