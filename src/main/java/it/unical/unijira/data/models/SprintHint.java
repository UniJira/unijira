package it.unical.unijira.data.models;


import it.unical.unijira.data.models.items.Item;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SprintHint extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn
    private User targetUser;

    @ManyToOne
    @JoinColumn
    private Item targetItem;

    @ManyToOne
    @JoinColumn
    private Sprint sprint;


    @Column
    @Enumerated(EnumType.STRING)
    private HintType type;



}
