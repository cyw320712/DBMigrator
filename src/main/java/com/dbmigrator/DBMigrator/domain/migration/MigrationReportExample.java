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
@Table(name = "Report", schema = "public")
public class MigrationReportExample extends BaseMigrationEntity {
    @Id
    private Long reportId;

    private Long reporterId;

    private Long targetId;

    private Long postId;

    private String title;

    private String type;

    private String content;

    public MigrationReportExample(Date regDate, Date modDate, Long reportId, Long reporterId, Long targetId, Long postId, String title, String type, String content) {
        super(regDate, modDate);
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.targetId = targetId;
        this.postId = postId;
        this.title = title;
        this.type = type;
        this.content = content;
    }
}
