package com.dbmigrator.DBMigrator;

import com.dbmigrater.DBMigrater.domain.legacy.thignsflow.LegacyUserRepository;
import com.dbmigrater.DBMigrater.domain.legacy.thignsflow.LegacyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    public void init(){
        initService.initdb();
    }

    @Component
    @RequiredArgsConstructor
    static class InitService {
        private final LegacyUserRepository em;

        @Transactional
        public void initdb() {
            for (int i = 1; i < 10000; i++){
                em.save(
                        LegacyUser.builder()
                                .id(Integer.toString(i))
                                .userId(new Long(i))
                                .name("test" + Integer.toString(i))
                                .email("test@Test.com")
                                .build()
                );
            }
        }
    }
}
