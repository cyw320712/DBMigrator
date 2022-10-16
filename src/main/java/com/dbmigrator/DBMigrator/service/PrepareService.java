package com.dbmigrator.DBMigrator.service;

import com.dbmigrator.DBMigrator.repository.BaseLegacyRepository;
import com.dbmigrator.DBMigrator.repository.BaseMigrationRepository;
import com.dbmigrator.DBMigrator.utils.RepositoryFactoryPostProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.*;

@RequiredArgsConstructor
@Service
public class PrepareService {
    private final EntityManager em;
    private final MongoMappingContext mappingContext;
    private ConfigurableApplicationContext currentBeanContext;
    private HashMap<String, BaseLegacyRepository> legacyRepositoryManager;
    private HashMap<String, BaseMigrationRepository> migrationRepositoryManager;
    private Boolean readyToMigration = false;

    public void readyMigration(){
        if (!readyToMigration){
            Set<EntityType<?>> jpaEntityList = em.getMetamodel().getEntities();
            Set<MongoPersistentEntity<?>> mongoEntityList = new HashSet<>(mappingContext.getPersistentEntities());

            ConfigurableListableBeanFactory beanFactory = currentBeanContext.getBeanFactory();

            RepositoryFactoryPostProcessor jpaRepositoryFactory = new RepositoryFactoryPostProcessor(jpaEntityList, mongoEntityList);
            jpaRepositoryFactory.postProcessBeanFactory(beanFactory);

            String[] currentBeanDefinitions = currentBeanContext.getBeanDefinitionNames();

            for(String s : currentBeanDefinitions)
                System.out.println(s);
            legacyRepositoryManager = getLegacyRepositories(currentBeanDefinitions);
            migrationRepositoryManager = getMigrationRepositories(currentBeanDefinitions);

            readyToMigration = true;
        }
        else{
            System.out.println("Already Created and injected Dynamic Repositories!");
        }
    }

    public HashMap<String, BaseLegacyRepository> getLegacyRepositoryManager(){
        return legacyRepositoryManager;
    }

    public HashMap<String, BaseMigrationRepository> getMigrationRepositoryManager(){
        return migrationRepositoryManager;
    }

    private HashMap<String, BaseLegacyRepository> getLegacyRepositories(String[] currentBeanDefinitions) {
        List<String> legacyRepositoryNameList = getTargetRepositoryNameList(currentBeanDefinitions, "legacy");
        int repositoryLen = "ExampleRepository".length();

        HashMap<String, BaseLegacyRepository> resultMap = new HashMap<>();

        legacyRepositoryNameList
                .forEach(beanName -> resultMap.put(
                                beanName.substring(6, beanName.length() - repositoryLen),
                                (BaseLegacyRepository) currentBeanContext.getBean(beanName)
                        )
                );

        return resultMap;
    }

    private HashMap<String, BaseMigrationRepository> getMigrationRepositories(String[] currentBeanDefinitions) {
        List<String> migrationRepositoryNameList = getTargetRepositoryNameList(currentBeanDefinitions, "migration");
        int repositoryLen = "ExampleRepository".length();

        HashMap<String, BaseMigrationRepository> resultMap = new HashMap<>();

        migrationRepositoryNameList
                .forEach(beanName -> resultMap.put(
                        beanName.substring(9, beanName.length() - repositoryLen),
                        (BaseMigrationRepository) currentBeanContext.getBean(beanName)
                    )
                );

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
