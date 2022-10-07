package com.dbmigrator.DBMigrator.domain.legacy;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;
import com.dbmigrator.DBMigrator.domain.migration.MigrationCommentExample;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@NoArgsConstructor
@Getter
@Document(collection = "Comment")
public class LegacyCommentExample implements BaseLegacyEntity {
    @Id
    private String id;

    @Indexed
    private Long commentId;

    private Long postId;

    private Long userId;

    private Long likeCount;

    private String comment;

    private Date regDate;

    private Date modDate;

    @Override
    public MigrationCommentExample convert() {
        return new MigrationCommentExample(regDate, modDate, commentId, postId, userId, likeCount, comment);
    }

    public LegacyCommentExample(String id, Long commentId, Long postId, Long userId, Long like, String comment, Date regDate, Date modDate) {
        this.id = id;
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.likeCount = like;
        this.comment = comment;
        this.regDate = regDate;
        this.modDate = modDate;
    }
}
