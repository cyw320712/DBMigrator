package com.dbmigrator.DBMigrator.domain.migration;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "user", schema = "public")
public class MigrationUser extends BaseTimeEntity {

    @Id
    private Long userId;

    private String email;

    private String name;

    private String type;

    private int coin;

    @Builder
    public MigrationUser(Long userId, String email, String name, String type, int coin){
        super();

        this.userId = userId;
        this.email = email;
        this.name = name;
        this.type = type;
        this.coin = coin;
    }
}
