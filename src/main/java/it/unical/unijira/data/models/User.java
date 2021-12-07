package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table
@Getter @Setter @ToString
public class User extends AbstractBaseEntity {

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

    @OneToMany
    @ToString.Exclude
    private List<Notify> notifies;

    @OneToMany
    @ToString.Exclude
    private List<Project> ownedProjects;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Member> members = new ArrayList<>();


    public List<GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

}
