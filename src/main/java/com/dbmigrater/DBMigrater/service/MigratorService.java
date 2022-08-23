package com.dbmigrater.DBMigrater.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MigratorService {

    private final int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2;
    private final List<List<Object>> taskQueue;
    private ConfigurableApplicationContext currentBeanContext;

    public String migrate() {
        readyMigration();

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

    private void readyMigration() {

        String[] currentBeanDefinitions = currentBeanContext.getBeanDefinitionNames();

        List<MongoRepository> legacyRepositoryList = getLegacyRepositories(currentBeanDefinitions);
        List<JpaRepository> migratedRepositoryList = getMigratedRepositories(currentBeanDefinitions);

        extractTasks(legacyRepositoryList, migratedRepositoryList);
    }

    private List<JpaRepository> getMigratedRepositories(String[] currentBeanDefinitions) {
        List<String> migratedRepositoryNameList = getTargetRepositoryNameList(currentBeanDefinitions, "migrated");

        List<JpaRepository> migratedRepositoryList = migratedRepositoryNameList.stream()
                .map(lr -> (JpaRepository) currentBeanContext.getBean(lr))
                .collect(Collectors.toList());

        return migratedRepositoryList;
    }

    private List<MongoRepository> getLegacyRepositories(String[] currentBeanDefinitions) {
        List<String> legacyRepositoryNameList = getTargetRepositoryNameList(currentBeanDefinitions, "legacy");

        List<MongoRepository> legacyRepositoryList = legacyRepositoryNameList.stream()
                .map(lr -> (MongoRepository) currentBeanContext.getBean(lr))
                .collect(Collectors.toList());

        return legacyRepositoryList;
    }

    private List<String> getTargetRepositoryNameList(String[] currentBeanDefinitions, String legacy) {
        return Arrays.stream(currentBeanDefinitions).filter(ob -> isTargetRepository(ob, legacy)).toList();
    }

    private boolean isTargetRepository(String ob, String state) {
        return ob.contains("Repository") && ob.contains(state);
    }

    private void extractTasks(List<MongoRepository> legacyRepositoryList, List<JpaRepository> migratedRepositoryList) {
        for(int i = 0; i < legacyRepositoryList.size(); i++) {
            List<Object> repositoryPair = new ArrayList<Object>();
            repositoryPair.add(legacyRepositoryList.get(i));
            repositoryPair.add(migratedRepositoryList.get(i));
            taskQueue.add(repositoryPair);
        }
    }

    @EventListener
    public void applicationReadyListener(ApplicationReadyEvent event) {
        currentBeanContext = event.getApplicationContext();
    }
}
