package it.unical.unijira.services.auth;

import it.unical.unijira.data.dao.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public record AuthUserDetailsService(UserRepository userRepository) implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository().findByUsername(username)
                .map(i -> new User(i.getUsername(), i.getPassword(), i.getAuthorities()))
                .orElseThrow(() -> new UsernameNotFoundException(username));

    }


}
