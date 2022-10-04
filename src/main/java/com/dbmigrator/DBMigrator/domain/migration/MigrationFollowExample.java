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
@Table(name = "follow", schema = "public")
public class MigrationFollowExample extends BaseMigrationEntity {
    @Id
    private Long followId;

    private Long followerId;

    private Long followingId;

    public MigrationFollowExample(Date regDate, Date modDate, Long followId, Long followerId, Long followingId) {
        super(regDate, modDate);
        this.followId = followId;
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
