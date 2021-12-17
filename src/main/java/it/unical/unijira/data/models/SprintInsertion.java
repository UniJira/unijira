package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table
@ToString
public class SprintInsertion extends AbstractBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;


    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private Sprint sprint;


    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private ProductBacklogItem pbi;
}
