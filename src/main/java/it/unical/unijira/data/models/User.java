package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table
@Getter @Setter
public class User {

    public enum Role {
        ADMIN,
        USER
    }


    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private boolean active;



    public List<GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

}
