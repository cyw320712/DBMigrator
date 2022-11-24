package com.dbmigrator.DBMigrator.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dbmigrator.DBMigrator.domain.legacy.LegacyCommentExample;
import com.dbmigrator.DBMigrator.domain.legacy.LegacyMenuExample;
import com.dbmigrator.DBMigrator.domain.legacy.LegacyPostExample;
import com.dbmigrator.DBMigrator.domain.legacy.LegacyReportExample;
import com.dbmigrator.DBMigrator.domain.legacy.LegacyUserExample;
import com.dbmigrator.DBMigrator.repository.BaseLegacyRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MigratorServiceTest {

    @Autowired
    MigratorService migratorService;
    @Autowired
    PrepareService prepareService;
    @Autowired
    EntityManager em;
    @Autowired
    ConfigurableApplicationContext currentBeanContext;
    private HashMap<String, BaseLegacyRepository> legacyRepositoryManager;

    void setRepositoryManagers() {
        legacyRepositoryManager = prepareService.getLegacyRepositoryManager();
    }

    @Test
    public void singleMigration() {
        //given
        prepareService.readyMigration();
        setRepositoryManagers();
        int light = 1000;
        List<LegacyUserExample> dummyUsers = createDummyUser(light);
        MongoRepository legacyUserRepository = legacyRepositoryManager.get("User");
        legacyUserRepository.saveAll(dummyUsers);
        List<String> targetEntityList = Arrays.asList("User");

        //when
        long beforeTime = System.currentTimeMillis();
        List<Progress> result = migratorService.migrate(targetEntityList);
        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        long secDiffTime = (afterTime - beforeTime) / 1000; //두 시간에 차 계산
        System.out.println("시간차이(m) : " + secDiffTime);

        //then
        boolean flag = true;

        for (Progress progress : result) {
            if (!progress.getValue()) {
                flag = false;
                break;
            }
        }

        assertTrue(flag);
    }

    @Test
    public void lightMigrate() {
        // given
        prepareService.readyMigration();
        setRepositoryManagers();
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

        List<String> targetEntityList = Arrays.asList("User", "Post", "Comment", "Menu", "Report");

        // when
        // 대상 EntityList에 대해서 Migration 준비
        // 받아온 Progress 객체를 result 에 저장하기
        long beforeTime = System.currentTimeMillis();
        List<Progress> result = migratorService.migrate(targetEntityList);
        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        long secDiffTime = (afterTime - beforeTime) / 1000; //두 시간에 차 계산
        System.out.println("시간차이(m) : " + secDiffTime);

        // then
        boolean flag = true;

        for (Progress progress : result) {
            if (!progress.getValue()) {
                flag = false;
                break;
            }
        }

        assertTrue(flag);
        // Repository로 전체 Entity가 migration 됐는지 확인하기
    }

    @Test
    public void heavyMigrate() {
        prepareService.readyMigration();
        setRepositoryManagers();
        int heavy = 10000;
        // Entity 당 10000개씩 더미 데이터 생성
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

        List<String> targetEntityList = Arrays.asList("User", "Post", "Comment", "Menu", "Report");

        // when
        // 대상 EntityList에 대해서 Migration 준비
        // 받아온 Progress 객체를 result 에 저장하기
        long beforeTime = System.currentTimeMillis();
        List<Progress> result = migratorService.migrate(targetEntityList);
        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        long secDiffTime = (afterTime - beforeTime) / 1000; //두 시간에 차 계산
        System.out.println("시간차이(m) : " + secDiffTime);

        // then
        boolean flag = true;

        for (Progress progress : result) {
            if (!progress.getValue()) {
                flag = false;
                break;
            }
        }

        assertTrue(flag);
        // Repository로 전체 Entity가 migration 됐는지 확인하기
    }

    private List<LegacyUserExample> createDummyUser(int num) {
        List<LegacyUserExample> result = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            String id = Integer.toString(i);
            Long userId = (long) i;
            String name = "test" + Integer.toString(i);
            String email = "test@Test.com";
            String type = "test";
            Date regDate = new Date();
            int coin = i;
            LegacyUserExample newUser = new LegacyUserExample(id, userId, name, email, type, regDate, coin);
            result.add(newUser);
        }

        return result;
    }

    private List<LegacyPostExample> createDummyPost(int num) {
        List<LegacyPostExample> result = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            String id = Integer.toString(i);
            Long postId = (long) i;
            Long userId = (long) getRandomId(num);
            Long menuId = (long) getRandomId(num);
            String title = "test" + Integer.toString(i);
            String content = "testTesttEstteSttesT";
            Long view = (long) getRandomId(num);
            Date regDate = new Date();
            Date modDate = new Date();
            LegacyPostExample newPost = new LegacyPostExample(id, postId, userId, menuId, title, content, view, regDate,
                    modDate);
            result.add(newPost);
        }

        return result;
    }

    private List<LegacyCommentExample> createDummyComment(int num) {
        List<LegacyCommentExample> result = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            String id = Integer.toString(i);
            Long commentId = (long) i;
            Long postId = (long) getRandomId(num);
            Long userId = (long) getRandomId(num);
            Long like = (long) getRandomId(num);
            String comment = "testsetsetsetset";
            Date regDate = new Date();
            Date modDate = new Date();
            LegacyCommentExample newComment = new LegacyCommentExample(id, commentId, postId, userId, like, comment,
                    regDate, modDate);
            result.add(newComment);
        }

        return result;
    }

    private List<LegacyMenuExample> createDummyMenu(int num) {
        List<LegacyMenuExample> result = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            String id = Integer.toString(i);
            Long userId = (long) 0;
            Long menuId = (long) i;
            String title = "test" + Integer.toString(i);
            Long order = (long) getRandomId(num);
            Date regDate = new Date();
            Date modDate = new Date();
            LegacyMenuExample newMenu = new LegacyMenuExample(id, menuId, title, order, userId, regDate, modDate);
            result.add(newMenu);
        }

        return result;
    }

    private List<LegacyReportExample> createDummyReport(int num) {
        List<LegacyReportExample> result = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            String id = Integer.toString(i);
            Long reportId = (long) i;
            Long reporterId = (long) getRandomId(num);
            Long targetId = (long) getRandomId(num);
            Long postId = (long) i;
            String title = "test" + Integer.toString(i);
            String type = "test";
            String content = "testTesttEstteSttesT";
            Date regDate = new Date();
            Date modDate = new Date();
            LegacyReportExample newReport = new LegacyReportExample(id, reportId, reporterId, targetId, postId, title,
                    type, content, regDate, modDate);
            result.add(newReport);
        }

        return result;
    }

    private int getRandomId(int limit) {
        Random rd = new Random();
        return (int) (rd.nextInt(limit - 1) + 1);
    }
}