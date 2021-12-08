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
public class Note {

    @Id
    @GeneratedValue
    private Long id;


    @Column
    @Basic(optional = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    @Column
    @Basic(optional = false)
    private LocalDateTime updatedAt = LocalDateTime.now();


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
