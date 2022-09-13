package com.dbmigrator.DBMigrator.service;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Migrator implements Callable<List<String>> {
    private final List<List<Object>> task;

    public Migrator(List<List<Object>> task) {
        this.task = task;
    }

    @Transactional(readOnly = true)
    public List<String> call() {
        return task.stream().map(this::migrate).collect(Collectors.toList());
    }

    private String migrate(List<Object> target) {
        MongoRepository legacyRepository = (MongoRepository) target.get(0);
        JpaRepository migrationRepository = (JpaRepository) target.get(1);

        try {
            List<BaseLegacyEntity> objects = legacyRepository.findAll();

            objects.forEach((object) -> {
                migrationRepository.save(object.convert());
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "Complete";
    }
}
