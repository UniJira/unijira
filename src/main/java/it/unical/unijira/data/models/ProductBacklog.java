package it.unical.unijira.data.models;

import it.unical.unijira.data.models.projects.Project;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;


@Entity
@Table
@ToString
public class ProductBacklog extends AbstractBaseEntity{

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;


    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private Project project;


    @OneToMany(mappedBy = "backlog", cascade = CascadeType.ALL)
    @Getter
    @Setter
    @ToString.Exclude
    private List<Sprint> sprints;

    @OneToMany(mappedBy = "backlog", cascade = CascadeType.ALL)
    @Getter
    @Setter
    @ToString.Exclude
    private List<Roadmap> roadmaps;
}
