package it.unical.unijira.data.models;

import lombok.*;

import javax.persistence.*;
import java.net.URL;
import java.time.LocalDateTime;

@Entity
@Table
@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class Notify extends AbstractBaseEntity {

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum Mask {

        ACCOUNT_CREATED,
        ACCOUNT_UPDATED_CREDENTIALS,
        ACCOUNT_UPDATED_PROFILE,
        ACCOUNT_UPDATED_STATUS,

        PROJECT_CREATED,
        PROJECT_INVITE_RECEIVED,
        PROJECT_INVITE_ACCEPTED,
        PROJECT_ON_JOINED_NEW_MEMBER,
        PROJECT_ON_CREATED_NEW_TICKET,
        PROJECT_ON_CREATED_NEW_RELEASE,
        PROJECT_ON_CREATED_NEW_SPRINT,
        PROJECT_ON_UPDATED_TICKET,
        PROJECT_ON_UPDATED_RELEASE,
        PROJECT_ON_UPDATED_SPRINT,

        REPORT_SENT,
        REPORT_RECEIVED,
        REPORT_UPDATED,
        REPORT_CLOSED,

    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 256)
    @Basic(optional = false)
    private String title;

    @Column(length = 4096)
    @Basic(optional = false)
    private String message;

    @Column(length = 1024)
    private URL target;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private Priority priority = Priority.LOW;

    @ManyToOne
    @JoinColumn
    private User user;

    @Column
    private boolean read = false;

    @Column
    private LocalDateTime date = LocalDateTime.now();

}
