package com.dbmigrater.DBMigrater;

import com.dbmigrater.DBMigrater.domain.legacy.LegacyUserRepository;
import com.dbmigrater.DBMigrater.domain.legacy.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
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
                        User.builder()
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
