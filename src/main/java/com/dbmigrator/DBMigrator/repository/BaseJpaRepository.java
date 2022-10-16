package com.dbmigrator.DBMigrator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface BaseJpaRepository<T, ID> extends JpaRepository<T, ID> {
    Stream<T> findByIdBetween(ID from, ID to);
}
