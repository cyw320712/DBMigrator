package com.dbmigrator.DBMigrator.service;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Service
public class MigratorService {

    private final PrepareService prepareService;
    private boolean migrating = false;

    // TODO: 모니터링
    public List<Progress> getMigrationResult(List<String> targetEntityList) {
        List<Progress> result = new ArrayList<>();

        if (migrating) {
            // 마이그레이션 중인 경우, 얼만큼 마이그레이션이 진행됐는지 반환
        }
        else {
            result = migrate(targetEntityList);
        }
        return result;
    }

    public List<Progress> migrate(List<String> targetEntityList) {
        prepareService.readyMigration();
        migrating = true;

        Map<MongoRepository, JpaRepository> taskQueue = prepareService.getLegacyRepositoryManager().entrySet()
                .stream()
                .filter(e -> {
                    if (targetEntityList == null) return true;
                    else return targetEntityList.contains(e.getKey());
                })
                .collect(toMap(
                        e -> e.getValue(),
                        e -> (JpaRepository) prepareService.getMigrationRepositoryManager().get(e.getKey()))
                );

        List<Progress> results = taskQueue.entrySet().parallelStream()
                .map(entry -> {
                    MongoRepository legacyRepository = entry.getKey();
                    JpaRepository migrationRepository = entry.getValue();

                    Progress result = singleMigration(legacyRepository, migrationRepository);

                    return result;
                })
                .toList();

        return results;
    }

    private Progress singleMigration(MongoRepository legacyRepository, JpaRepository migrationRepository) {
        List<BaseLegacyEntity> legacyEntities;
        List<BaseMigrationEntity> migrationEntities;

        try {
            legacyEntities = legacyRepository.findAll();

            migrationEntities = legacyEntities.stream()
                    .map(BaseLegacyEntity::convert).toList();

            migrationRepository.saveAll(migrationEntities);
        }
        catch (Exception e) {
            return new Progress(false);
        }
        finally {
            // 불필요해진 Entity 데이터들 null out
            legacyEntities = null;
            migrationEntities = null;
        }

        return new Progress(true);
    }
}
