package it.unical.unijira.data.models.items;

import it.unical.unijira.data.models.AbstractBaseEntity;
import it.unical.unijira.data.models.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Getter @Setter @ToString
@Builder @AllArgsConstructor @NoArgsConstructor
public class EvaluationProposal extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String content;

    @Column
    private Integer evaluation;

    @ManyToOne
    @JoinColumn
    private User author;

    @ManyToOne
    @JoinColumn
    private Item ticket;

}
