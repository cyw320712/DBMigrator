package com.dbmigrator.DBMigrator.domain.legacy;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;
import com.dbmigrator.DBMigrator.domain.migration.MigrationUserExample;
import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Entity
@Document(collection = "User")
public class LegacyUserExample implements BaseLegacyEntity {

    @Id
    private String id;

    @Indexed
    private Long userId;

    private String email;

    private String name;

    private String type;

    private Date regDate;

    private int coin;

    @Override
    public BaseMigrationEntity convert() {
        MigrationUserExample migrationUser = new MigrationUserExample(userId, email, name, type, coin, regDate);

        return migrationUser;
    }

    public LegacyUserExample(String id, Long userId, String email, String name, String type, Date regDate, int coin) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.type = type;
        this.regDate = regDate;
        this.coin = coin;
    }
}
