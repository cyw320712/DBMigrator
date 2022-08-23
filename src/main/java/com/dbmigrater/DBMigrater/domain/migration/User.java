package com.dbmigrater.DBMigrater.domain.migration;

import com.dbmigrater.DBMigrater.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "user", schema = "public")
public class User {

    @Id
    private Long userId;

    private String email;

    private String name;

    @Builder
    public User(Long userId, String email, String name){
        this.userId = userId;
        this.email = email;
        this.name = name;
    }
}
