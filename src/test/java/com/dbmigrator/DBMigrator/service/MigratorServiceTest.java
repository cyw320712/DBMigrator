package com.dbmigrator.DBMigrator.service;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;
import com.dbmigrator.DBMigrator.utils.RepositoryFactoryPostProcessor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.hibernate.metamodel.model.domain.internal.EntityTypeImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MigratorServiceTest {

    @Autowired MigratorService migratorService;
    @Autowired
    EntityManager em;

    @Test
    public void lightMigrate() throws Exception {
        //given
        // Entity 생성 (10 ~ 20 종류)
        // EntityManager에 등록하기
        // Entity 당 1000개씩 더미 데이터 생성
        for (int i = 0; i < 20; i++) {
            Map<Class, Class> dummyEntitySet = createDummyEntitySet(i);
        }
        // Repository를 사용해 저장하기

        //when
        // migrate 호출
        // 받아온 Progress 객체를 result로 저장하기

        //then
        Set<EntityType<?>> result = em.getMetamodel().getEntities();
        result.stream().forEach(entity -> {
            System.out.println(entity.getName());
        });
        assertTrue(true);
        // assertEqual(result, new Progress(Entity));
        // Repository로 전체 Entity가 migration 됐는지 확인하기
    }

    @Test
    public void heavyMigrate() throws Exception {
        //given
        // Entity 생성 (100 ~ 120 종류)
        // EntityManager에 등록하기
        // Entity 당 1만개씩 더미 데이터 생성

        //when
        // migrate 호출
        // 받아온 Progress 객체를 result로 저장하기

        //then
        // assertEqual(result, new Progress(Entity));
        // 전체 Entity 마이그레이션 됐는지 확인하기
    }

    private Map<Class, Class> createDummyEntitySet(Integer i){
        String legacyName = "LegacyDummy" + i.toString() + "Entity";
        String migrationName = "MigrationDummy" + i.toString() + "Entity";

        Map<Class, Class> resultSet = new HashMap<>();

        Class<?> legacyEntity = new ByteBuddy()
                .subclass(BaseLegacyEntity.class)
                .name(legacyName)
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        Class<?> migrationEntity = new ByteBuddy()
                .subclass(BaseMigrationEntity.class)
                .name(migrationName)
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        resultSet.put(legacyEntity, migrationEntity);

        return resultSet;
    }

    private void registerEntity(List<Class> entityClassList){

        Set<EntityType<?>> entityTypeList = entityClassList.stream()
                .map((entityClass) -> {
                    return new EntityTypeImpl<?>(entityClass);
                })
                .collect(Collectors.toSet());

        RepositoryFactoryPostProcessor rfpp = new RepositoryFactoryPostProcessor(entityTypeList);
    }

    public class LegacyDummyEntity implements BaseLegacyEntity {


        @Override
        public BaseMigrationEntity convert() {
            return null;
        }
    }
}