package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.projects.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProjectRepository extends JpaRepository<Membership, Long> {
}
