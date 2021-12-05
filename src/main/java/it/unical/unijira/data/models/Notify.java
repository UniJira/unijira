package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.net.URL;
import java.time.LocalDateTime;

@Entity
@Table
@Getter @Setter @ToString
public class Notify {

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @Basic(optional = false)
    private String title;

    @Column
    @Basic(optional = false)
    private String message;

    @Column
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
