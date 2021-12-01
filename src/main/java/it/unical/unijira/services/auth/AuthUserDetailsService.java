package it.unical.unijira.services.auth;

import it.unical.unijira.data.dao.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public record AuthUserDetailsService(UserRepository userRepository) implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository().findByUsername(username)
                .map(i -> new User(i.getUsername(), i.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(i.getRole().name()))))
                .orElseThrow(() -> new UsernameNotFoundException(username));

    }


    public void authenticate(String username, String password) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, password));
    }

}
