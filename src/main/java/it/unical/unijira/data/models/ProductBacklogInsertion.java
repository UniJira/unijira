package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table
@ToString
public class ProductBacklogInsertion extends AbstractBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private ProductBacklog backlog;

    @OneToOne
    @JoinColumn
    @Getter
    private Item item;

    @Column
    @Basic(optional = false)
    @Getter
    @Setter
    private Integer priority;

}
