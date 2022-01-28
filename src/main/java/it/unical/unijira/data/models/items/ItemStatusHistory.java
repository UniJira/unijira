package it.unical.unijira.data.models.items;

import it.unical.unijira.data.models.AbstractBaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemStatusHistory extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    private Item item;

    @Column
    @Enumerated(EnumType.STRING)
    private ItemStatus oldStatus;

    @Basic(optional = false)
    @Column
    @Enumerated(EnumType.STRING)
    private ItemStatus newStatus;

    @Basic(optional = false)
    @Column
    @Getter @Setter
    private LocalDateTime changeDate;
}
