package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query(value ="Select ext_m.key.user " +
            "from Membership ext_m " +
            "where ext_m.key.user <> :currentUser " +
            "and ext_m.key.project in" +
                "(SELECT p " +
                "FROM User u, Membership m, Project p " +
                "where m.key.project = p and m.key.user = :currentUser)")
    List<User> findCollaborators(User currentUser);

}
