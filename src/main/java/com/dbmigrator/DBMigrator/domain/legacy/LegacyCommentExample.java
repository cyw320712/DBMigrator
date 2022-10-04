package com.dbmigrator.DBMigrator.domain.legacy;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;
import com.dbmigrator.DBMigrator.domain.migration.MigrationCommentExample;
import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Entity
@Document(collection = "Comment")
public class LegacyCommentExample implements BaseLegacyEntity {
    @Id
    private String id;

    @Indexed
    private Long commentId;

    private Long postId;

    private Long userId;

    private Integer like;

    private String comment;

    private Date regDate;

    private Date modDate;

    @Override
    public MigrationCommentExample convert() {
        return new MigrationCommentExample(regDate, modDate, commentId, postId, userId, like, comment);
    }
}
