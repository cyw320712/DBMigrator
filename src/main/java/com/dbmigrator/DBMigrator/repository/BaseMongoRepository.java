package com.dbmigrator.DBMigrator.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.stream.Stream;

public interface BaseMongoRepository<T, ID> extends MongoRepository<T, ID> {
    Stream<T> findAllByOrderByIdAsc();
}
