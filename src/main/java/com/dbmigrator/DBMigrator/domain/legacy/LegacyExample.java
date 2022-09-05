package com.dbmigrator.DBMigrator.domain.legacy;

import com.dbmigrator.DBMigrator.domain.migration.MigrationUser;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@Getter
@Document(collection = "User")
public class LegacyExample implements BaseLegacyEntity {

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
    public LegacyExample(String id, Long userId, String email, String name, String type, Date regDate, int coin) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.type = type;
        this.regDate = regDate;
        this.coin = coin;
    }

    @Override
    public Object convert() {
        MigrationUser migrationUser = new MigrationUser(userId, email, name, type, coin);

        return migrationUser;
    }
}
