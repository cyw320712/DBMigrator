package com.dbmigrator.DBMigrator.domain.migration;

import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@NoArgsConstructor
@Getter
@Entity
@Table(name = "Post", schema = "public")
public class MigrationPostExample extends BaseMigrationEntity {
    public MigrationPostExample(Long postId, Long userId, Long menuId, String title, String content, Long view, Date regDate, Date modDate) {
        super(regDate, modDate);

        this.postId = postId;
        this.userId = userId;
        this.menuId = menuId;
        this.title = title;
        this.content = content;
        this.view = view;
    }

    @Id
    private Long postId;

    private Long userId;

    private Long menuId;

    private String title;

    private String content;

    private Long view;
}
