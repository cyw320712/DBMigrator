package com.dbmigrator.DBMigrator.domain.legacy;

import com.dbmigrator.DBMigrator.domain.common.BaseLegacyEntity;
import com.dbmigrator.DBMigrator.domain.common.BaseMigrationEntity;
import com.dbmigrator.DBMigrator.domain.migration.MigrationFollowExample;
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
@Document(collection = "Follow")
public class LegacyFollowExample implements BaseLegacyEntity {
    @Id
    private String id;

    @Indexed
    private Long followId;

    private Long followerId;

    private Long followingId;

    private Date regDate;

    private Date modDate;


    @Override
    public MigrationFollowExample convert() {
        return new MigrationFollowExample(regDate, modDate, followId, followerId, followingId);
    }
}
