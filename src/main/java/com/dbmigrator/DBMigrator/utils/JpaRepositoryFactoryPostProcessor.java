package com.dbmigrator.DBMigrator.utils;

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

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
public class JpaRepositoryFactoryPostProcessor implements BeanFactoryPostProcessor {

    private Set<EntityType<?>> entityClassList;
    private final EntityManager em;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        entityClassList = em.getMetamodel().getEntities();

        List<String> jpaEntityDefinitions = entityClassList.stream().map(EntityType::getName).toList();

        jpaEntityDefinitions.forEach((entityDefinition) -> {
            Class<?> dynamicJpaRepository = createDynamicJpaRepository(entityDefinition);

            registerJpaRepositoryFactoryBean(dynamicJpaRepository, (DefaultListableBeanFactory) beanFactory);
        });
    }

    private void registerJpaRepositoryFactoryBean(Class<?> jpaRepositoryClass, DefaultListableBeanFactory defaultListableBeanFactory) {
        String beanName = convertCamelToBeanName(jpaRepositoryClass.getName());

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(JpaRepositoryFactoryBean.class)
                .addConstructorArgValue(jpaRepositoryClass);

        System.out.println(beanDefinitionBuilder.getBeanDefinition().getBeanClassName());

        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    private Class<?> createDynamicJpaRepository(String className) {
        String repositoryName = className + "Repository";
        EntityType<?> entityClass = findTargetEntityClass(className);

        TypeDescription.Generic genericType = null;
        genericType = TypeDescription.Generic.Builder
                .parameterizedType(JpaRepository.class, entityClass.getJavaType(), Long.class)
                .build();

        Loaded<?> generatedClass = new ByteBuddy()
                .makeInterface()
                .implement(genericType)
                .name(repositoryName)
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION);

        return generatedClass.getLoaded();
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
