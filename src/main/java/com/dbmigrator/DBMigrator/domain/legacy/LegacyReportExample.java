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
@Entity
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
}
