package it.unical.unijira.data.models.projects;

import it.unical.unijira.data.models.AbstractBaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DefinitionOfDoneEntry extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @Basic(optional = false)
    private String description;

    @Column
    @Basic(optional = false)
    private Integer priority;

    @ManyToOne
    @JoinColumn
    private Project project;
}
