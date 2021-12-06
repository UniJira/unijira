package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProjectRepository extends JpaRepository<Member, Long> {
}
