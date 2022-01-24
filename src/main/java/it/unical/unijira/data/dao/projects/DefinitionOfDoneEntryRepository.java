package it.unical.unijira.data.dao.projects;

import it.unical.unijira.data.models.projects.DefinitionOfDoneEntry;
import it.unical.unijira.data.models.projects.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DefinitionOfDoneEntryRepository extends JpaRepository<DefinitionOfDoneEntry, Long> {

    List<DefinitionOfDoneEntry> findAllByProjectId(Long id, Pageable pageable);

    @Query("select max(dod.priority) from DefinitionOfDoneEntry dod where dod.project = :project")
    Optional<Integer> getLastPriorityByProject(Project project);

    List<DefinitionOfDoneEntry> findAllByProjectAndPriorityIsGreaterThan(Project project, Integer priority);

    Optional<DefinitionOfDoneEntry> findByProjectIdAndPriority(Long id, Integer priority);
}
