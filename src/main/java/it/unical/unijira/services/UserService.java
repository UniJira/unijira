package it.unical.unijira.services;

import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {

    @Autowired
    public UserService {}


    public Optional<User> findByUsername(String username) {
        return userRepository().findByUsername(username);
    }

    public Optional<User> save(User user) {


        var password = user.getPassword();

        // Matches at least one number
        if(!password.matches("(?=.*[0-9]).*"))
            return Optional.empty();

        // Matches at least one lower case letter
        if(!password.matches("(?=.*[a-z]).*"))
            return Optional.empty();

        // Matches at least one upper case letter
        if(!password.matches("(?=.*[A-Z]).*"))
            return Optional.empty();

        user.setPassword(passwordEncoder.encode(password));


        return Optional.of(userRepository().saveAndFlush(user));

    }
}
