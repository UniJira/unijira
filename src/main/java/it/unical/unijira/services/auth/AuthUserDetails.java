package it.unical.unijira.services.auth;

import it.unical.unijira.data.models.User;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class AuthUserDetails extends org.springframework.security.core.userdetails.User {

    private final User model;

    public AuthUserDetails(User model, org.springframework.security.core.userdetails.UserDetails userDetails) {

        super(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.isEnabled(),
                userDetails.isAccountNonExpired(),
                userDetails.isCredentialsNonExpired(),
                userDetails.isAccountNonLocked(),
                userDetails.getAuthorities()
        );

        this.model = Objects.requireNonNull(model);

    }


}
