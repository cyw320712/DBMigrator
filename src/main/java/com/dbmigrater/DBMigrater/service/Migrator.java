package com.dbmigrater.DBMigrater.service;

import com.dbmigrater.DBMigrater.domain.legacy.LegacyUserRepository;
import com.dbmigrater.DBMigrater.domain.migration.MigrationUserRepository;
import com.dbmigrater.DBMigrater.domain.migration.User;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.List;

public class Migrator implements Runnable {
    private final EntityManager entityManager;
    private final List<List<Object>> task;

    public Migrator(EntityManager entityManager, List<List<Object>> task) {
        this.entityManager = entityManager;
        this.task = task;
    }

    @Transactional(readOnly = true)
    public void run() {
        List<Object> currentTask = task.get(0);
        LegacyUserRepository legacyRepository = (LegacyUserRepository) currentTask.get(0);
        MigrationUserRepository migrationRepository = (MigrationUserRepository) currentTask.get(1);

        try {
            Long start = new Long(1);
            Long end = new Long(10000);
            List<com.dbmigrater.DBMigrater.domain.legacy.User> objects = legacyRepository.findUserByUserIdBetween(start, end);

            objects.forEach( object -> {
                User convertedObject = convertDataByType(object);

                migrationRepository.save(convertedObject);
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private User convertDataByType(com.dbmigrater.DBMigrater.domain.legacy.User user) {
        // TODO: 신경써야하는 타입 변환 정리하기
        // 1. Convert json string to Json 등

        Field[] legacyFields = user.getClass().getFields();
        Field[] migrationFields = User.class.getFields();

        return User.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
