package com.dbmigrator.DBMigrator.utils;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
public class JpaRepositoryFactoryPostProcessor implements BeanFactoryPostProcessor {

    private final String MIGRATION_PATH = "C:\\Users\\CYW\\Desktop\\Java\\DBMigrater\\src\\main\\java\\com\\dbmigrator\\DBMigrator\\domain\\migration";
    private final EntityManager entityManager;
    private Set<EntityType<?>> entityClassList;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("pass postProcessBeanFactory");
        entityClassList = entityManager.getMetamodel().getEntities();

        List<String> jpaEntityDefinitions = getEntityList();

        jpaEntityDefinitions.forEach((entityDefinition) -> {
            String repositoryName = entityDefinition + "Repository";

            Class<?> dynamicJpaRepository = createDynamicJpaRepository(entityDefinition);

            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(dynamicJpaRepository);

            ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(repositoryName, beanDefinitionBuilder.getBeanDefinition());
            // 지금 여기서 등록이 안됨
        });

        System.out.println("=================================");
    }

    private List<String> getEntityList() {
        File dir = new File(MIGRATION_PATH);

        File[] files = dir.listFiles();

        return Arrays.stream(files).map((file) -> {
            String filename = file.getName();

            return filename.substring(0, 1).toLowerCase() + filename.substring(1, filename.length() - 5);
        }).filter((name) -> name.contains("migration") && name.contains("Entity")).toList();
    }

    private Class<?> createDynamicJpaRepository(String className) {
        EntityType<?> entityClass = null;
        String repositoryName = className + "Repository";
        for (EntityType<?> e : entityClassList) {
            if (e.getName().equalsIgnoreCase(className))
                entityClass = e;
        }

        if (entityClass == null) {
            throw new IllegalStateException("Entity Could not found");
        }

        TypeDescription.Generic genericType = TypeDescription.Generic.Builder
                .parameterizedType(JpaRepository.class, entityClass.getClass(), Long.class)
                .build();

        new ByteBuddy()
                .makeInterface()
                .implement(genericType)
                .name(repositoryName)
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION);

        try {
            return Class.forName(repositoryName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Repository Could not foudn");
        }
    }
}
