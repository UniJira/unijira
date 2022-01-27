package it.unical.unijira.data.models;

import it.unical.unijira.data.models.items.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table
@ToString
public class SprintInsertion extends AbstractBaseEntity{

    @Id
    @GeneratedValue
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
    private Item item;
}
