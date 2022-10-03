package com.dbmigrator.DBMigrator.utils;

import com.mongodb.lang.Nullable;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Loaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;

import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
public class RepositoryFactoryPostProcessor implements BeanFactoryPostProcessor {

    // 주입받은 EntityClassList 에 대해서 처리하기
    private final Set<EntityType<?>> entityClassList;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        List<String> mongoEntityDefinitions = getLegacyEntityDefinitions();
        List<String> jpaEntityDefinitions = getMigrationEntityDefinitions();

        mongoEntityDefinitions.forEach((entityDefinition) -> {
            Class<?> dynamicMongoRepository = createDynamicRepository(entityDefinition, MongoRepository.class);

            registerMongoRepositoryFactoryBean(dynamicMongoRepository, (DefaultListableBeanFactory) beanFactory);
        });

        jpaEntityDefinitions.forEach((entityDefinition) -> {
            Class<?> dynamicJpaRepository = createDynamicRepository(entityDefinition, JpaRepository.class);

            registerJpaRepositoryFactoryBean(dynamicJpaRepository, (DefaultListableBeanFactory) beanFactory);
        });
    }

    private void registerMongoRepositoryFactoryBean(Class<?> mongoRepositoryClass, DefaultListableBeanFactory defaultListableBeanFactory) {
        String beanName = convertCamelToBeanName(mongoRepositoryClass.getName());

        MongoTemplate mongoTemplate = defaultListableBeanFactory.getBean(MongoTemplate.class);

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

    private Class<?> createDynamicRepository(String entityName, Class<?> repositoryClass) {
        String repositoryName = entityName + "Repository";

        Class<?> entityClass = findTargetEntityClass(entityName);
        Class<?> idClass = getIdClass(repositoryClass);

        TypeDescription.Generic genericType = buildRepositoryTypeDescription(repositoryClass, entityClass, idClass);
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

    private List<String> getLegacyEntityDefinitions() {
        return entityClassList.stream()
                .map(EntityType::getName)
                .filter(name -> name.contains("Legacy"))
                .toList();
    }

    private List<String> getMigrationEntityDefinitions() {
        return entityClassList.stream()
                .map(EntityType::getName)
                .filter(name -> name.contains("Migration"))
                .toList();
    }

    private Class<?> findTargetEntityClass(String entityName) {
        EntityType<?> entityClass = null;

        for (EntityType<?> e : entityClassList) {
            if (e.getName().equalsIgnoreCase(entityName))
                entityClass = e;
        }

        if (entityClass == null) {
            throw new IllegalStateException("Entity does not exist");
        }

        return entityClass.getJavaType();
    }

    private Class<?> getIdClass(Class<?> repositoryClass) {
        return repositoryClass == JpaRepository.class ? Long.class : String.class;
    }

    private String convertCamelToBeanName(String camelName) {
        return camelName.substring(0, 1).toLowerCase() + camelName.substring(1);
    }
}
