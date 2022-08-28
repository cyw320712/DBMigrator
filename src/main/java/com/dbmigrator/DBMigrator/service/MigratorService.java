package com.dbmigrator.DBMigrator.service;

import com.dbmigrater.DBMigrater.common.BaseLegacyEntity;
import com.dbmigrater.DBMigrater.common.BaseTimeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.EntityManagerBeanDefinitionRegistrarPostProcessor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MigratorService {
    private List<List<Object>> taskQueue;
    private Iterator<List<Object>> taskIter;
    private ConfigurableApplicationContext currentBeanContext;
    private ClassLoader classLoader;
    private final int THREAD_NUM = 10;

    public String migrate() {
        readyMigration();

        int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2;

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        executor.execute(new Migrator(distributeTask(threadPoolSize)));

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
        classLoader = Thread.currentThread().getContextClassLoader();

        List<String> legacyEntities = extractSpecifiedEntities("Legacy");
        List<String> migrationEntities = extractSpecifiedEntities("Migration");

        System.out.println(legacyEntities.size() + migrationEntities.size());

        registerMongoRepository(legacyEntities);
        registerJpaRepository(migrationEntities);

        List<MongoRepository> legacyRepositoryList = getLegacyRepositories(currentBeanDefinitions);
        List<JpaRepository> migratedRepositoryList = getMigratedRepositories(currentBeanDefinitions);

        taskQueue = extractTasks(legacyRepositoryList, migratedRepositoryList);
        taskIter = taskQueue.iterator();
    }

    private void registerMongoRepository(List<String> Entities) {
        Entities.forEach(entity -> {
            EntityManagerBeanDefinitionRegistrarPostProcessor embd = new EntityManagerBeanDefinitionRegistrarPostProcessor();
            embd.postProcessBeanFactory();
            SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(JpaRepository.class);

            Class<?> entityClass = entity.getClass();
            beanDefinition.setTargetType(JpaRepository<entityClass, Long>.class);
            beanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);

            registry.registerBeanDefinition(entity + "Repository", beanDefinition);
        });
    }

    private void registerJpaRepository(List<String> Entities) {
        Entities.forEach(entity -> {
            SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
            RootBeanDefinition beanDefinition = new RootBeanDefinition(JpaRepository.class);

            Class<?> entityClass = entity.getClass();
            beanDefinition.setTargetType(JpaRepository<entityClass, Long>.class);
            beanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);

            registry.registerBeanDefinition(entity + "Repository", beanDefinition);
        });
    }

    private List<String> extractSpecifiedEntities(String specify) {
        List<String> specifiedEntities = new ArrayList<>();
        String path = "src/main/java/com/dbmigrater/DBMigrater/domain/" + specify.toLowerCase() + "/thingsflow/";

        File[] files = new File(path).listFiles();

        for(File f: Objects.requireNonNull(files)){
            if (f.isDirectory()) continue;
            String fileName = f.getName();
            String entity = fileName.substring(0, fileName.length() - 5);
            specifiedEntities.add(entity);
        }

        return specifiedEntities;
    }

    private List<List<Object>> distributeTask(int size) {
        System.out.println(size);
        List<List<Object>> subTaskQueue = new ArrayList<>();

        int count = 0;
        while (count < size && taskIter.hasNext()) {
            List<Object> task = taskIter.next();
            subTaskQueue.add(task);
            count++;
        }

        System.out.println(subTaskQueue);
        return subTaskQueue;
    }

    private List<JpaRepository> getMigratedRepositories(String[] currentBeanDefinitions) {
        List<String> migrationRepositoryNameLIst = getTargetRepositoryNameList(currentBeanDefinitions, "migration");

        return migrationRepositoryNameLIst.stream()
                .map(mr -> (JpaRepository) currentBeanContext.getBean(mr))
                .collect(Collectors.toList());
    }

    private List<MongoRepository> getLegacyRepositories(String[] currentBeanDefinitions) {
        List<String> legacyRepositoryNameList = getTargetRepositoryNameList(currentBeanDefinitions, "legacy");

        return legacyRepositoryNameList.stream()
                .map(lr -> (MongoRepository) currentBeanContext.getBean(lr))
                .collect(Collectors.toList());
    }

    private List<String> getTargetRepositoryNameList(String[] currentBeanDefinitions, String legacy) {
        return Arrays.stream(currentBeanDefinitions).filter(ob -> isTargetRepository(ob, legacy)).toList();
    }

    private boolean isTargetRepository(String ob, String state) {
        return ob.contains("Repository") && ob.contains(state);
    }

    private List<List<Object>> extractTasks(List<MongoRepository> legacyRepositoryList, List<JpaRepository> migratedRepositoryList) {
        List<List<Object>> extractedTasks = new ArrayList<>();

        for(int i = 0; i < legacyRepositoryList.size(); i++) {
            List<Object> repositoryPair = new ArrayList<Object>();

            repositoryPair.add(legacyRepositoryList.get(i));
            repositoryPair.add(migratedRepositoryList.get(i));

            extractedTasks.add(repositoryPair);
        }

        return extractedTasks;
    }

    @EventListener
    public void applicationReadyListener(ApplicationReadyEvent event) {
        currentBeanContext = event.getApplicationContext();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
