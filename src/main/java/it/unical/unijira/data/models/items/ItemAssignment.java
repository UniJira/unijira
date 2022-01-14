package it.unical.unijira.data.models.items;

import it.unical.unijira.data.models.AbstractBaseEntity;
import it.unical.unijira.data.models.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemAssignment extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn
    private Item item;

    @ManyToOne
    @JoinColumn
    private User assignee;

}
