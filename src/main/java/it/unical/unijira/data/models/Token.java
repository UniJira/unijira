package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Getter @Setter @ToString
public class Token {

    public enum TokenType {
        AUTHORIZATION,
        ACCOUNT_CONFIRM,
        ACCOUNT_RESET_PASSWORD,
    }

    @Id
    private String id;

    @OneToOne
    @JoinColumn
    private User user;

    @Column
    private LocalDateTime expireDate;

    @Column
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;


}
