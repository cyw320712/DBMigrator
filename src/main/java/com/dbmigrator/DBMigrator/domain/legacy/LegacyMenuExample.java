package com.dbmigrator.DBMigrator.domain.legacy;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import com.dbmigrator.DBMigrator.domain.migration.MigrationMenuExample;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@NoArgsConstructor
@Getter
@Document(collection = "Menu")
public class LegacyMenuExample implements BaseLegacyEntity {
    @Id
    private String id;

    @Indexed
    private Long menuId;

    private String title;

    private Long turn;

    private Long userId;

    private Date regDate;

    private Date modDate;

    @Override
    public MigrationMenuExample convert() {
        return new MigrationMenuExample(regDate, modDate, menuId, title, turn, userId);
    }

    public LegacyMenuExample(String id, Long menuId, String title, Long order, Long userId, Date regDate, Date modDate) {
        this.id = id;
        this.menuId = menuId;
        this.title = title;
        this.turn = order;
        this.userId = userId;
        this.regDate = regDate;
        this.modDate = modDate;
    }
}
