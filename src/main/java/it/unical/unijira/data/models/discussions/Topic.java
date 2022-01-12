package it.unical.unijira.data.models.discussions;

import it.unical.unijira.data.models.AbstractBaseEntity;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.projects.Project;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Topic extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @Basic(optional = false)
    private String title;

    @Column
    @Basic(optional = false)
    private String content;


    @ManyToOne
    @JoinColumn
    private Project project;

    @ManyToOne
    @JoinColumn
    private User author;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "topic")
    private List<Message> messages = new ArrayList<>();

}
