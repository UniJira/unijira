package it.unical.unijira.data.models.items;

import it.unical.unijira.data.models.AbstractBaseEntity;
import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemDefinitionOfDone extends AbstractBaseEntity {
    @EmbeddedId
    private ItemDefinitionOfDoneKey key;
}
