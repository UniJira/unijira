package it.unical.unijira.data.models.items;

import it.unical.unijira.data.models.projects.DefinitionOfDoneEntry;
import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;


@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemDefinitionOfDoneKey implements Serializable {

    @ManyToOne
    @JoinColumn
    private DefinitionOfDoneEntry definitionOfDoneEntry;

    @ManyToOne
    @JoinColumn
    private Item item;
}
