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
@Table(name = "comment", schema = "public")
public class MigrationCommentExample extends BaseMigrationEntity {
    @Id
    private Long commentId;

    private Long postId;

    private Long userId;

    private Integer like;

    private String comment;

    public MigrationCommentExample(Date regDate, Date modDate, Long commentId, Long postId, Long userId, Integer like, String comment) {
        super(regDate, modDate);

        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.like = like;
        this.comment = comment;
    }
}
