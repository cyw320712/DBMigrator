package com.dbmigrator.DBMigrator;

import com.dbmigrator.DBMigrator.domain.legacy.LegacyExample;
import com.dbmigrator.DBMigrator.domain.legacy.LegacyExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.initDb();
    }

    @Component
    @RequiredArgsConstructor
    static class InitService {
        private final LegacyExampleRepository em;

        @Transactional()
        public void initDb() {
            for (int i = 1; i < 10000; i++){
                em.save(
                        LegacyExample.builder()
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
