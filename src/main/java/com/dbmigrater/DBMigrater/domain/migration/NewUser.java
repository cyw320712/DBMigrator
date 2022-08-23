package com.dbmigrater.DBMigrater.domain.migration;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "user", schema = "public")
public class NewUser extends BaseTimeEntity {

    @Id
    private Long userId;

    private String email;

    private String name;

    private String type;

    private int coin;

    @Builder
    public NewUser(Long userId, String email, String name, String type, int coin){
        super();

        this.userId = userId;
        this.email = email;
        this.name = name;
        this.type = type;
        this.coin = coin;
    }
}
