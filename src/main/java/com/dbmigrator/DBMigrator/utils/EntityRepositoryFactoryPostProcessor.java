package com.dbmigrator.DBMigrator.utils;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Loaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.mongodb.core.MongoOperationsExtensionsKt;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
public class EntityRepositoryFactoryPostProcessor implements BeanFactoryPostProcessor {

    private Set<EntityType<?>> entityClassList;
    private final EntityManager em;
    private final MongoTemplate mongoTemplate;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        entityClassList = em.getMetamodel().getEntities();

        List<String> mongoEntityDefinitions = getLegacyEntityDefinitions();
        List<String> jpaEntityDefinitions = getMigrationEntityDefinitions();

        mongoEntityDefinitions.forEach((entityDefinition) -> {
            Class<?> dynamicMongoRepository = createDynamicMongoRepository(entityDefinition);

            registerMongoRepositoryFactoryBean(dynamicMongoRepository, (DefaultListableBeanFactory) beanFactory);
        });

        jpaEntityDefinitions.forEach((entityDefinition) -> {
            Class<?> dynamicJpaRepository = createDynamicJpaRepository(entityDefinition);

            registerJpaRepositoryFactoryBean(dynamicJpaRepository, (DefaultListableBeanFactory) beanFactory);
        });

        for (String s : beanFactory.getBeanDefinitionNames())
            System.out.println(s);
    }

    private List<String> getLegacyEntityDefinitions() {
        return entityClassList.stream().map(EntityType::getName).filter(name -> name.contains("Legacy")).toList();
    }

    private List<String> getMigrationEntityDefinitions() {
        return entityClassList.stream().map(EntityType::getName).filter(name -> name.contains("Migration")).toList();
    }

    private void registerMongoRepositoryFactoryBean(Class<?> mongoRepositoryClass, DefaultListableBeanFactory defaultListableBeanFactory) {
        String beanName = convertCamelToBeanName(mongoRepositoryClass.getName());

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(MongoRepositoryFactoryBean.class)
                .addConstructorArgValue(mongoRepositoryClass)
                .addPropertyValue("mongoOperations", mongoTemplate);

        System.out.println(beanDefinitionBuilder.getBeanDefinition().getBeanClassName());

        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    private void registerJpaRepositoryFactoryBean(Class<?> jpaRepositoryClass, DefaultListableBeanFactory defaultListableBeanFactory) {
        String beanName = convertCamelToBeanName(jpaRepositoryClass.getName());

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(JpaRepositoryFactoryBean.class)
                .addConstructorArgValue(jpaRepositoryClass);

        System.out.println(beanDefinitionBuilder.getBeanDefinition().getBeanClassName());

        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    private Class<?> createDynamicMongoRepository(String className) {
        String repositoryName = className + "Repository";
        EntityType<?> entityClass = findTargetEntityClass(className);

        TypeDescription.Generic genericType = buildRepositoryTypeDescription(MongoRepository.class, entityClass.getJavaType(), String.class);

        Loaded<?> generatedClass = dynamicCreateClassAndLoad(repositoryName, genericType);

        return generatedClass.getLoaded();
    }

    private Class<?> createDynamicJpaRepository(String className) {
        String repositoryName = className + "Repository";
        EntityType<?> entityClass = findTargetEntityClass(className);

        TypeDescription.Generic genericType = buildRepositoryTypeDescription(JpaRepository.class, entityClass.getJavaType(), Long.class);

        Loaded<?> generatedClass = dynamicCreateClassAndLoad(repositoryName, genericType);

        return generatedClass.getLoaded();
    }

    private TypeDescription.Generic buildRepositoryTypeDescription(Class<?> repositoryClass, Class<?> entityClass, Class<?> idClass) {
        return TypeDescription.Generic.Builder
                .parameterizedType(repositoryClass, entityClass, idClass)
                .build();
    }

    private Loaded<?> dynamicCreateClassAndLoad(String repositoryName, TypeDescription.Generic genericType) {
        return new ByteBuddy()
                .makeInterface()
                .implement(genericType)
                .name(repositoryName)
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION);
    }

    private EntityType<?> findTargetEntityClass(String className) {
        EntityType<?> entityClass = null;

        for (EntityType<?> e : entityClassList) {
            if (e.getName().equalsIgnoreCase(className))
                entityClass = e;
        }

        if (entityClass == null) {
            throw new IllegalStateException("Entity does not exist");
        }

        return entityClass;
    }

    private String convertCamelToBeanName(String camelName) {
        return camelName.substring(0, 1).toLowerCase() + camelName.substring(1);
    }
}
