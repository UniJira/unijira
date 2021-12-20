package it.unical.unijira.data.dao;


import it.unical.unijira.data.models.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwnerId(Long id, Pageable pageable);

    List<Project> findByMembershipsKeyUserId(Long id, Pageable pageable);

}
