package com.dbmigrator.DBMigrator.service;

import com.dbmigrator.DBMigrator.utils.EntityRepositoryFactoryPostProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MigratorService {

    private final int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2;
    private final List<List<Object>> taskQueue;
    private final EntityManager em;
    private ConfigurableApplicationContext currentBeanContext;

    public String migrate() throws InterruptedException {
        readyMigration();

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        List<Callable<List<String>>> taskList = new ArrayList<>();

        int perThread = (int) Math.ceil((double)taskQueue.size() / threadPoolSize );

        for (int i = 0; i < perThread; i++){
            List<List<Object>> subTaskQueue = new ArrayList<>();

            for (int j = i * threadPoolSize; j < (i+1) * threadPoolSize && j < taskQueue.size(); j++)
                subTaskQueue.add(taskQueue.get(j));

            Callable<List<String>> task = new Migrator(subTaskQueue);
            taskList.add(task);
        }

        List<Future<List<String>>> resultList = executor.invokeAll(taskList);

        while (resultList.size() != taskQueue.size()) {
            Thread.sleep(5000);
        }

        return "Complete";
    }

    private void readyMigration() {
        ConfigurableListableBeanFactory beanFactory = currentBeanContext.getBeanFactory();

        EntityRepositoryFactoryPostProcessor repositoryFactory = new EntityRepositoryFactoryPostProcessor(em);
        repositoryFactory.postProcessBeanFactory(beanFactory);

        String[] currentBeanDefinitions = currentBeanContext.getBeanDefinitionNames();

        List<MongoRepository> legacyRepositoryList = getLegacyRepositories(currentBeanDefinitions);
        List<JpaRepository> migratedRepositoryList = getMigrationRepositories(currentBeanDefinitions);

        System.out.println(legacyRepositoryList.size());
        System.out.println(migratedRepositoryList.size());

        extractTasks(legacyRepositoryList, migratedRepositoryList);
    }

    private List<JpaRepository> getMigrationRepositories(String[] currentBeanDefinitions) {
        List<String> migrationRepositoryNameList = getTargetRepositoryNameList(currentBeanDefinitions, "migration");

        return migrationRepositoryNameList.stream()
                .map(lr -> (JpaRepository) currentBeanContext.getBean(lr))
                .collect(Collectors.toList());
    }

    private List<MongoRepository> getLegacyRepositories(String[] currentBeanDefinitions) {
        List<String> legacyRepositoryNameList = getTargetRepositoryNameList(currentBeanDefinitions, "legacy");

        return legacyRepositoryNameList.stream()
                .map(lr -> (MongoRepository) currentBeanContext.getBean(lr))
                .collect(Collectors.toList());
    }

    private List<String> getTargetRepositoryNameList(String[] currentBeanDefinitions, String repositoryType) {
        return Arrays.stream(currentBeanDefinitions).filter(ob -> isTargetRepository(ob, repositoryType)).toList();
    }

    private boolean isTargetRepository(String ob, String repositoryType) {
        return ob.contains("Repository") && ob.contains(repositoryType);
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
