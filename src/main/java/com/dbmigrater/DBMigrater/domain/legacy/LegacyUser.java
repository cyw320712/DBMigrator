package com.dbmigrater.DBMigrater.domain.legacy;

import com.dbmigrater.DBMigrater.domain.migration.MigrationUserRepository;
import com.dbmigrater.DBMigrater.domain.migration.NewUser;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@Getter
@Document(collection = "User")
public class LegacyUser implements BaseLegacyEntity {

    @Id
    private String id;

    @Indexed
    private Long userId;

    private String email;

    private String name;

    private String type;

    private Date regDate;

    private int coin;


    @Builder
    public LegacyUser(String id, Long userId, String email, String name, String type, Date regDate, int coin) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.type = type;
        this.regDate = regDate;
        this.coin = coin;
    }

    @Override
    public Object convertAndMigration() {
        NewUser newUser = new NewUser(userId, email, name, type, coin);

        return newUser;
    }
}
