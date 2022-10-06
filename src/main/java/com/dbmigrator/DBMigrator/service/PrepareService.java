package com.dbmigrator.DBMigrator.service;

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
import javax.persistence.metamodel.EntityType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class PrepareService {
    private final EntityManager em;
    private ConfigurableApplicationContext currentBeanContext;
    private HashMap<String, MongoRepository> legacyRepositoryManager;
    private HashMap<String, JpaRepository> migrationRepositoryManager;
    private Boolean readyToMigration = false;

    public void readyMigration(){
        if (!readyToMigration){
            Set<EntityType<?>> entityList = em.getMetamodel().getEntities();
            ConfigurableListableBeanFactory beanFactory = currentBeanContext.getBeanFactory();

            RepositoryFactoryPostProcessor repositoryFactory = new RepositoryFactoryPostProcessor(entityList);
            repositoryFactory.postProcessBeanFactory(beanFactory);

            String[] currentBeanDefinitions = currentBeanContext.getBeanDefinitionNames();

            legacyRepositoryManager = getLegacyRepositories(currentBeanDefinitions);
            migrationRepositoryManager = getMigrationRepositories(currentBeanDefinitions);

            readyToMigration = true;
        }
        else{
            System.out.println("Already Created and injected Dynamic Repositories!");
        }
    }

    public HashMap<String, MongoRepository> getLegacyRepositoryManager(){
        return legacyRepositoryManager;
    }

    public HashMap<String, JpaRepository> getMigrationRepositoryManager(){
        return migrationRepositoryManager;
    }

    private HashMap<String, MongoRepository> getLegacyRepositories(String[] currentBeanDefinitions) {
        List<String> legacyRepositoryNameList = getTargetRepositoryNameList(currentBeanDefinitions, "legacy");
        int repositoryLen = "ExampleRepository".length();

        HashMap<String, MongoRepository> resultMap = new HashMap<>();

        legacyRepositoryNameList
                .forEach(beanName -> resultMap.put(beanName.substring(6, beanName.length() - repositoryLen), (MongoRepository) currentBeanContext.getBean(beanName)));

        return resultMap;
    }

    private HashMap<String, JpaRepository> getMigrationRepositories(String[] currentBeanDefinitions) {
        List<String> migrationRepositoryNameList = getTargetRepositoryNameList(currentBeanDefinitions, "migration");
        int repositoryLen = "ExampleRepository".length();

        HashMap<String, JpaRepository> resultMap = new HashMap<>();

        migrationRepositoryNameList
                .forEach(beanName -> resultMap.put(beanName.substring(9, beanName.length() - repositoryLen), (JpaRepository) currentBeanContext.getBean(beanName)));

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
