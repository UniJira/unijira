package it.unical.unijira.data.models;

import it.unical.unijira.data.models.projects.Membership;
import it.unical.unijira.data.models.projects.Project;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table
@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class User extends AbstractBaseEntity {

    public static final Long CURRENT_USER_ID = 0L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    @Basic(optional = false)
    private String username;

    @Column(nullable = false)
    @Basic(optional = false)
    private String password;

    @Column
    private boolean activated = false;

    @Column
    private boolean disabled = false;

    @Column
    private URL avatar;

    @OneToMany
    @ToString.Exclude
    private List<Notify> notifications;

    @OneToMany
    @ToString.Exclude
    private List<Project> ownedProjects;

    @OneToMany(mappedBy = "key.user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Membership> memberships = new ArrayList<>();


    public List<GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /* Campi per riempire la sezione profilo utente */

    @Column
    private LocalDate birthDate;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String role;

    @Column
    private String description;

}
