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

    @OneToMany
    @ToString.Exclude
    private List<Document> documents;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "key.project", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Membership> memberships = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProductBacklog> backlogs = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Release> releases = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<DefinitionOfDoneEntry> definitionOfDone = new ArrayList<>();

}
