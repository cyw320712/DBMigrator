package com.dbmigrater.DBMigrater.service;

import com.dbmigrater.DBMigrater.domain.legacy.LegacyUserLegacyRepository;
import com.dbmigrater.DBMigrater.domain.migration.MigrationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class MigratorService {

    private final LegacyUserLegacyRepository legacyUserRepository;
    private final MigrationUserRepository migrationUserRepository;

    private final int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2;
    private final List<List<Object>> taskQueue;

    public String migrate() {
        List<Object> userPair = new ArrayList<Object>();
        userPair.add(legacyUserRepository);
        userPair.add(migrationUserRepository);
        taskQueue.add(userPair);

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        executor.execute(new Migrator(taskQueue));

        try {
            executor.awaitTermination(10L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
        }

        System.out.println("Porting Completed");
        return "Complete";
    }
}
