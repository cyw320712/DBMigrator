package com.dbmigrator.DBMigrator.domain.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@NoArgsConstructor
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseMigrationEntity {
    @CreatedDate
    private Date regDate;

    @LastModifiedDate
    private Date modDate;

    public BaseMigrationEntity(Date regDate){
        this.regDate = regDate;
    }

    public BaseMigrationEntity(Date regDate, Date modDate) {
        this.regDate = regDate;
        this.modDate = modDate;
    }
}
