package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Getter @Setter
public class Token {

    public enum TokenType {
        ACCOUNT_CONFIRM,
        ACCOUNT_RESET_PASSWORD,
    }

    @Id
    private String id;

    @OneToOne
    private User user;

    @Column
    private LocalDateTime expireDate;

    @Column
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

}
