package com.dbmigrator.DBMigrator.domain.migration;

import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "Menu", schema = "public")
public class MigrationMenuExample extends BaseMigrationEntity {
    @Id
    private Long menuId;

    private String title;

    private Long turn;

    private Long userId;

    public MigrationMenuExample(Date regDate, Date modDate, Long menuId, String title, Long turn, Long userId) {
        super(regDate, modDate);
        this.menuId = menuId;
        this.title = title;
        this.turn = turn;
        this.userId = userId;
    }
}
