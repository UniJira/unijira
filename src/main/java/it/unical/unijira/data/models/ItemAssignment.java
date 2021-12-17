package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@ToString
public class ItemAssignment extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn
    private ProductBacklogItem item;

    @ManyToOne
    @JoinColumn
    private User assignee;

}
