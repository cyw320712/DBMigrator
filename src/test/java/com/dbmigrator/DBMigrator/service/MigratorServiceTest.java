package com.dbmigrator.DBMigrator.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MigratorServiceTest {

    @Test
    public void lightMigrate() throws Exception {
        //given
        // Entity 생성 (10 ~ 20 종류)
        // EntityManager에 등록하기
        // Entity 당 1000개씩 더미 데이터 생성

        //when
        // migrate 호출

        //then
        // 전체 Entity 마이그레이션 됐는지 확인하기
    }

    @Test
    public void heavyMigrate() throws Exception {
        //given
        // Entity 생성 (100 ~ 120 종류)
        // EntityManager에 등록하기
        // Entity 당 1만개씩 더미 데이터 생성

        //when
        // migrate 호출

        //then
        // 전체 Entity 마이그레이션 됐는지 확인하기
    }


}