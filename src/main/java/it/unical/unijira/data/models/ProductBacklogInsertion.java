package it.unical.unijira.data.models;

import it.unical.unijira.data.models.items.Item;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBacklogInsertion extends AbstractBaseEntity{

    @Id
    @GeneratedValue
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
    @Setter
    private Item item;

    @Column
    @Basic(optional = false)
    @Getter
    @Setter
    private Integer priority;

}
