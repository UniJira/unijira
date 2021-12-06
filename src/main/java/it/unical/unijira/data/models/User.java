package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table
@Getter @Setter @ToString
public class User {

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


    public List<GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

}
