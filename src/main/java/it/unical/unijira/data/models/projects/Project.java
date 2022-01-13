package it.unical.unijira.data.models.projects;

import it.unical.unijira.data.models.AbstractBaseEntity;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.projects.releases.Release;
import lombok.*;

import javax.persistence.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class Project extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @Basic(optional = false)
    private String name;

    @Column
    @Basic(optional = false)
    private String key;

    @Column
    private URL icon;

    @ManyToOne
    @JoinColumn
    private User owner;

    @OneToMany(mappedBy = "key.project", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Membership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ProductBacklog> backlogs = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Release> releases = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<DefinitionOfDoneEntry> definitionOfDone = new ArrayList<>();

}
