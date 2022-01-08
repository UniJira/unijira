package it.unical.unijira.services.common;

import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.projects.Project;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public interface UserService {

    List<User> findAll();
    List<User> findAll(Integer page, Integer size);

    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> save(User user);

    boolean activate(Long id);


    List<User> getCollaborators(User user);

    List<Project> getProjects(User user);
}
