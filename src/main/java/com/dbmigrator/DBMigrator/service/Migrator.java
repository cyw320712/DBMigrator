package com.dbmigrator.DBMigrator.service;

import com.dbmigrator.DBMigrator.domain.legacy.BaseLegacyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class Migrator implements Runnable {
    private final List<List<Object>> task;

    public Migrator(List<List<Object>> task) {
        this.task = task;
    }

    @Transactional(readOnly = true)
    public void run() {
        task.forEach((one) -> {
            migrate(one);
        });
    }

    private void migrate(List<Object> target) {
        MongoRepository legacyRepository = (MongoRepository) target.get(0);
        JpaRepository migrationRepository = (JpaRepository) target.get(1);

        try {
            List<BaseLegacyEntity> objects = legacyRepository.findAll();

            objects.forEach((object) -> {
                migrationRepository.save(object.convertAndMigration());
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }
}
