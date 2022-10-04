package com.dbmigrator.DBMigrator.domain.legacy;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;
import com.dbmigrator.DBMigrator.domain.migration.MigrationPostExample;
import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Entity()
@Document(collection = "Post")
public class LegacyPostExample implements BaseLegacyEntity {
    @Id
    private String id;

    @Indexed
    private Long postId;

    private Long userId;

    private Long menuId;

    private String title;

    private String content;

    private Integer view;

    private Date regDate;

    private Date modDate;

    @Override
    public MigrationPostExample convert() {
        return new MigrationPostExample(postId, userId, menuId, title, content, view, regDate, modDate);
    }
}
