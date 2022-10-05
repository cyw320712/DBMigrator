package com.dbmigrator.DBMigrator.service;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import com.dbmigrator.DBMigrator.domain.legacy.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MigratorServiceTest {

    @Autowired MigratorService migratorService;
    @Autowired
    EntityManager em;
    @Autowired
    ConfigurableApplicationContext currentBeanContext;
    HashMap<String, MongoRepository> legacyRepositoryManager;
    HashMap<String, JpaRepository> migrationRepositoryManager;

    @BeforeAll()
    private void setRepositoryManagers() {
        legacyRepositoryManager = migratorService.getLegacyRepositoryManager();
        migrationRepositoryManager = migratorService.getMigrationRepositoryManager();
    }

    @Test
    public void singleMigration() throws Exception {
        //given
        int light = 1000;
        List<LegacyUserExample> dummyUsers = createDummyUser(light);
        MongoRepository legacyUserRepository = legacyRepositoryManager.get("User");
        legacyUserRepository.saveAll(dummyUsers);
        List<Class> targetEntityList = Arrays.asList(LegacyUserExample.class);

        //when
        migratorService.readyPartialMigration(targetEntityList);
        List<Progress> result = migratorService.partialMigrate(targetEntityList);

        //then
        List<Progress> answer = new ArrayList<Progress>();
        for (int i=0; i<result.size(); i++)
            answer.add(new Progress(true));
        assertEquals(result, answer);
    }

    @Test
    public void lightMigrate() {
        // given
        int light = 1000;
        // Entity 당 1000개씩 더미 데이터 생성
        List<LegacyUserExample> dummyUsers = createDummyUser(light);
        List<LegacyPostExample> dummyPosts = createDummyPost(light);
        List<LegacyCommentExample> dummyComments = createDummyComment(light);
        List<LegacyMenuExample> dummyMenus = createDummyMenu(light);
        List<LegacyReportExample> dummyReports = createDummyReport(light);
        // Repository 가져오기
        MongoRepository legacyUserRepository = legacyRepositoryManager.get("User");
        MongoRepository legacyPostRepository = legacyRepositoryManager.get("Post");
        MongoRepository legacyCommentRepository = legacyRepositoryManager.get("Comment");
        MongoRepository legacyMenuRepository = legacyRepositoryManager.get("Menu");
        MongoRepository legacyReportRepository = legacyRepositoryManager.get("Report");
        // Repository를 사용해 저장
        legacyUserRepository.saveAll(dummyUsers);
        legacyPostRepository.saveAll(dummyPosts);
        legacyCommentRepository.saveAll(dummyComments);
        legacyMenuRepository.saveAll(dummyMenus);
        legacyReportRepository.saveAll(dummyReports);

        List<Class> targetEntityList = Arrays.asList(LegacyUserExample.class, LegacyPostExample.class, LegacyCommentExample.class, LegacyMenuExample.class, LegacyReportExample.class);

        // when
        // 대상 EntityList에 대해서 Migration 준비
        migratorService.readyPartialMigration(targetEntityList);
        // 받아온 Progress 객체를 result 에 저장하기
        List<Progress> result = migratorService.partialMigrate(targetEntityList);

        // then
        List<Progress> answer = new ArrayList<Progress>();
        for (int i=0; i<result.size(); i++)
            answer.add(new Progress(true));
        assertEquals(result, answer);
        // Repository로 전체 Entity가 migration 됐는지 확인하기
    }

    @Test
    public void heavyMigrate() throws Exception {
        int heavy = 100000;
        // Entity 당 100000개씩 더미 데이터 생성
        List<LegacyUserExample> dummyUsers = createDummyUser(heavy);
        List<LegacyPostExample> dummyPosts = createDummyPost(heavy);
        List<LegacyCommentExample> dummyComments = createDummyComment(heavy);
        List<LegacyMenuExample> dummyMenus = createDummyMenu(heavy);
        List<LegacyReportExample> dummyReports = createDummyReport(heavy);
        // Repository 가져오기
        MongoRepository legacyUserRepository = legacyRepositoryManager.get("User");
        MongoRepository legacyPostRepository = legacyRepositoryManager.get("Post");
        MongoRepository legacyCommentRepository = legacyRepositoryManager.get("Comment");
        MongoRepository legacyMenuRepository = legacyRepositoryManager.get("Menu");
        MongoRepository legacyReportRepository = legacyRepositoryManager.get("Report");
        // Repository를 사용해 저장
        legacyUserRepository.saveAll(dummyUsers);
        legacyPostRepository.saveAll(dummyPosts);
        legacyCommentRepository.saveAll(dummyComments);
        legacyMenuRepository.saveAll(dummyMenus);
        legacyReportRepository.saveAll(dummyReports);

        List<Class> targetEntityList = Arrays.asList(LegacyUserExample.class, LegacyPostExample.class, LegacyCommentExample.class, LegacyMenuExample.class, LegacyReportExample.class);

        // when
        // 대상 EntityList에 대해서 Migration 준비
        migratorService.readyPartialMigration(targetEntityList);
        // 받아온 Progress 객체를 result 에 저장하기
        List<Progress> result = migratorService.partialMigrate(targetEntityList);

        // then
        List<Progress> answer = new ArrayList<Progress>();
        for (int i=0; i<result.size(); i++)
            answer.add(new Progress(true));
        assertEquals(result, answer);
        // Repository로 전체 Entity가 migration 됐는지 확인하기
    }

    private List<LegacyUserExample> createDummyUser(int num){
        List<LegacyUserExample> result = new ArrayList<>();

        for (int i=1; i<=num; i++) {
            String id = Integer.toString(i);
            Long userId = (long) i;
            String name = "test"+ Integer.toString(i);
            String email = "test@Test.com";
            String type = "test";
            Date regDate = new Date();
            int coin = i;
            LegacyUserExample newUser = new LegacyUserExample(id, userId, name, email, type, regDate, coin);
            result.add(newUser);
        }

        return result;
    }

    private List<LegacyPostExample> createDummyPost(int num){
        List<LegacyPostExample> result = new ArrayList<>();

        for (int i=1; i<=num; i++) {
            String id = Integer.toString(i);
            Long postId = (long) i;
            Long userId = (long) getRandomId(num);
            Long menuId = (long) getRandomId(num);
            String title = "test"+ Integer.toString(i);
            String content = "testTesttEstteSttesT";
            Integer view = getRandomId(num);
            Date regDate = new Date();
            Date modDate = new Date();
            LegacyPostExample newPost = new LegacyPostExample(id, postId, userId, menuId, title, content, view, regDate, modDate);
            result.add(newPost);
        }

        return result;
    }

    private List<LegacyCommentExample> createDummyComment(int num){
        List<LegacyCommentExample> result = new ArrayList<>();

        for (int i=1; i<=num; i++) {
            String id = Integer.toString(i);
            Long commentId = (long) i;
            Long postId = (long) getRandomId(num);
            Long userId = (long) getRandomId(num);
            Integer like = getRandomId(num);
            String comment = "testsetsetsetset";
            Date regDate = new Date();
            Date modDate = new Date();
            LegacyCommentExample newComment = new LegacyCommentExample(id, commentId, postId, userId, like, comment, regDate, modDate);
            result.add(newComment);
        }

        return result;
    }

    private List<LegacyMenuExample> createDummyMenu(int num){
        List<LegacyMenuExample> result = new ArrayList<>();

        for (int i=1; i<=num; i++) {
            String id = Integer.toString(i);
            Long userId = (long) 0;
            Long menuId = (long) i;
            String title = "test"+ Integer.toString(i);
            Integer order = getRandomId(num);
            Date regDate = new Date();
            Date modDate = new Date();
            LegacyMenuExample newMenu = new LegacyMenuExample(id, menuId, title, order, userId, regDate, modDate);
            result.add(newMenu);
        }

        return result;
    }

    private List<LegacyReportExample> createDummyReport(int num){
        List<LegacyReportExample> result = new ArrayList<>();

        for (int i=1; i<=num; i++) {
            String id = Integer.toString(i);
            Long reportId = (long) i;
            Long reporterId = (long) getRandomId(num);
            Long targetId = (long) getRandomId(num);
            Long postId = (long) i;
            String title = "test"+ Integer.toString(i);
            String type = "test";
            String content = "testTesttEstteSttesT";
            Date regDate = new Date();
            Date modDate = new Date();
            LegacyReportExample newReport = new LegacyReportExample(id, reportId, reporterId, targetId, postId, title, type, content, regDate, modDate);
            result.add(newReport);
        }

        return result;
    }

    private int getRandomId(int limit){
        Random rd = new Random();
        return (int)(rd.nextInt(limit-1) + 1);
    }
}