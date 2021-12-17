package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@ToString
public class Note extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;


    @Column
    @Basic(optional = false)
    private LocalDateTime timestamp;


    @Column
    @Basic(optional = false)
    private String message;


    @ManyToOne
    @JoinColumn
    private Note replyTo;


    @ManyToOne
    @JoinColumn
    private ProductBacklogItem refersTo;


    @ManyToOne
    @JoinColumn
    private User author;


}
