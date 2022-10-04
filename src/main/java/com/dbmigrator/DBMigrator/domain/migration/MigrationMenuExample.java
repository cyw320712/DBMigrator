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
@Table(name = "menu", schema = "public")
public class MigrationMenuExample extends BaseMigrationEntity {
    @Id
    private Long menuId;

    private String title;

    private Integer order;

    private Long userId;

    public MigrationMenuExample(Date regDate, Date modDate, Long menuId, String title, Integer order, Long userId) {
        super(regDate, modDate);
        this.menuId = menuId;
        this.title = title;
        this.order = order;
        this.userId = userId;
    }
}
