package it.unical.unijira.services.auth;

import it.unical.unijira.data.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public record AuthUserDetailsService(UserRepository userRepository) implements UserDetailsService {

    @Autowired
    public AuthUserDetailsService {}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository().findByUsername(username)
                .map(i -> new AuthUserDetails(i, org.springframework.security.core.userdetails.User.builder()
                        .username(i.getUsername())
                        .password(i.getPassword())
                        .roles("USER")
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(i.isDisabled())
                        .build()))
                .orElseThrow(() -> new UsernameNotFoundException(username));

    }


}
