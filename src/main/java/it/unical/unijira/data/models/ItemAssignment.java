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
public class  ItemAssignment{


    @Id
    @GeneratedValue
    private Long id;


    @Column
    @Basic(optional = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    @Column
    @Basic(optional = false)
    private LocalDateTime updatedAt = LocalDateTime.now();


    @ManyToOne
    @JoinColumn
    private ProductBacklogItem item;


    @ManyToOne
    @JoinColumn
    private User assignee;

}
