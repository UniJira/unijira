package it.unical.unijira.services;

import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record UserService(UserRepository userRepository) {

    @Autowired
    public UserService {}

    public Optional<User> save(User user) {
        return Optional.of(userRepository().saveAndFlush(user));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository().findByUsername(username);
    }

}
