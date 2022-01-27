package it.unical.unijira.data.models;

import it.unical.unijira.data.models.projects.Document;
import it.unical.unijira.data.models.projects.Membership;
import it.unical.unijira.data.models.projects.Project;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
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

    public enum Status {
        ACTIVE,
        REQUIRE_CONFIRM,
        REQUIRE_PASSWORD
    }

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

    @Column(nullable = false)
    @Basic(optional = false)
    private Status status;

    @Column
    @Builder.Default
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
    @Builder.Default
    private List<Membership> memberships = new ArrayList<>();

    @OneToMany
    @ToString.Exclude
    private List<Document> documents;

    public List<GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /* Campi per riempire la sezione profilo utente */

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String role;

    @Column
    private String description;

    @Column
    private String github;

    @Column
    private String linkedin;

    @Column
    private String phoneNumber;
    
    @Column
    private String preferedTheme;
    
    @Column
    private String preferedLanguage;

}
