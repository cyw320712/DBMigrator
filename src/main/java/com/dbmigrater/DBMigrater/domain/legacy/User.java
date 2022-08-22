package com.dbmigrater.DBMigrater.domain.legacy;

import com.dbmigrater.DBMigrater.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Document(collection = "User")
public class User extends BaseTimeEntity {

    @Id()
    private String id;

    @Indexed
    private Long userId;

    private String email;

    private String name;

    @Builder
    public User(String id, Long userId, String email, String name) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.name = name;
    }
}
