package com.dbmigrator.DBMigrator.domain.migration;

import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "user", schema = "public")
public class MigrationUserExample extends BaseMigrationEntity {

    @Id
    private Long userId;

    private String email;

    private String name;

    private String type;

    private int coin;

    @Builder
    public MigrationUserExample(Long userId, String email, String name, String type, int coin, Date regDate){
        super(regDate);

        this.userId = userId;
        this.email = email;
        this.name = name;
        this.type = type;
        this.coin = coin;
    }
}
