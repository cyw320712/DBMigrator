package com.dbmigrator.DBMigrator.domain.legacy;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import com.dbmigrator.DBMigrator.domain.migration.MigrationReportExample;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@NoArgsConstructor
@Getter
@Document(collection = "Report")
public class LegacyReportExample implements BaseLegacyEntity {
    @Id
    private String id;

    @Indexed
    private Long reportId;

    private Long reporterId;

    private Long targetId;

    private Long postId;

    private String title;

    private String type;

    private String content;

    private Date regDate;

    private Date modDate;


    @Override
    public MigrationReportExample convert() {
        return new MigrationReportExample(regDate, modDate, reportId, reporterId, targetId, postId, title, type, content);
    }

    public LegacyReportExample(String id, Long reportId, Long reporterId, Long targetId, Long postId, String title, String type, String content, Date regDate, Date modDate) {
        this.id = id;
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.targetId = targetId;
        this.postId = postId;
        this.title = title;
        this.type = type;
        this.content = content;
        this.regDate = regDate;
        this.modDate = modDate;
    }
}
