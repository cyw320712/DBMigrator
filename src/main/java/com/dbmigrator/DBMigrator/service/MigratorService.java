package com.dbmigrator.DBMigrator.service;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;
import com.dbmigrator.DBMigrator.utils.RepositoryFactoryPostProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Service
public class MigratorService {

    private final EntityManager em;
    private ConfigurableApplicationContext currentBeanContext;
    private HashMap<String, MongoRepository> legacyRepositoryManager;
    private HashMap<String, JpaRepository> migrationRepositoryManager;

    public String migrate() {
        readyMigration();

        Map<MongoRepository, JpaRepository> taskQueue = legacyRepositoryManager.entrySet()
                .stream()
                .collect(toMap(
                        e -> e.getValue(),
                        e -> (JpaRepository) migrationRepositoryManager.get(e.getKey()))
                );

        taskQueue.entrySet()
                .forEach(entry -> {
                    MongoRepository legacyRepository = entry.getKey();
                    JpaRepository migrationRepository = entry.getValue();
                    List<BaseLegacyEntity> legacyEntities = legacyRepository.findAll();
                    List<BaseMigrationEntity> migrationEntities = legacyEntities.stream().map(BaseLegacyEntity::convert).toList();
                    migrationRepository.saveAll(migrationEntities);
                });
        
        return "Complete";
    }

    private void readyMigration() {
        ConfigurableListableBeanFactory beanFactory = currentBeanContext.getBeanFactory();

        RepositoryFactoryPostProcessor repositoryFactory = new RepositoryFactoryPostProcessor(em);
        repositoryFactory.postProcessBeanFactory(beanFactory);

        String[] currentBeanDefinitions = currentBeanContext.getBeanDefinitionNames();

        HashMap<String, MongoRepository> legacyRepositoryMap = getLegacyRepositories(currentBeanDefinitions);
        HashMap<String, JpaRepository> migratedRepositoryMap = getMigrationRepositories(currentBeanDefinitions);

        System.out.println(legacyRepositoryMap.entrySet());
        System.out.println(migratedRepositoryMap.entrySet());

        legacyRepositoryManager = getLegacyRepositories(currentBeanDefinitions);
        migrationRepositoryManager = getMigrationRepositories(currentBeanDefinitions);

    }

    private HashMap<String, JpaRepository> getMigrationRepositories(String[] currentBeanDefinitions) {
        List<String> migrationRepositoryNameList = getTargetRepositoryNameList(currentBeanDefinitions, "migration");

        HashMap<String, JpaRepository> resultMap = new HashMap<>();

        migrationRepositoryNameList
                .forEach(beanName -> resultMap.put(beanName.substring(9), (JpaRepository) currentBeanContext.getBean(beanName)));

        return resultMap;
    }

    private HashMap<String, MongoRepository> getLegacyRepositories(String[] currentBeanDefinitions) {
        List<String> legacyRepositoryNameList = getTargetRepositoryNameList(currentBeanDefinitions, "legacy");

        HashMap<String, MongoRepository> resultMap = new HashMap<>();

        legacyRepositoryNameList
                .forEach(beanName -> resultMap.put(beanName.substring(6), (MongoRepository) currentBeanContext.getBean(beanName)));

        return resultMap;
    }

    private List<String> getTargetRepositoryNameList(String[] currentBeanDefinitions, String repositoryType) {
        return Arrays.stream(currentBeanDefinitions).filter(ob -> isTargetRepository(ob, repositoryType)).toList();
    }

    private boolean isTargetRepository(String ob, String repositoryType) {
        return ob.contains("Repository") && ob.contains(repositoryType);
    }

    @EventListener
    public void applicationReadyListener(ApplicationReadyEvent event) {
        currentBeanContext = event.getApplicationContext();
    }
}
