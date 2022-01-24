package it.unical.unijira.data.models;

import lombok.*;

import javax.persistence.*;
import java.net.URL;

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

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Column(length = 4096)
    @Basic(optional = false)
    private String title;

    @Lob
    @Column(length = 4096)
    @Basic(optional = false)
    private String message;

    @Lob
    @Column(length = 4096)
    private URL target;

    @Column
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private Priority priority = Priority.LOW;

    @ManyToOne
    @JoinColumn
    private User user;

    @Column
    @Builder.Default
    private boolean read = false;

}
