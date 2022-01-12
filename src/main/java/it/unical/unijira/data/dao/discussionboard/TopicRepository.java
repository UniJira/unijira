package it.unical.unijira.data.dao.discussionboard;

import it.unical.unijira.data.models.discussionboard.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {

    @Query(value="FROM Topic t where t.id = :id and t.project.id = :projectId")
    Optional<Topic> findByIdAndProject(Long id, Long projectId);

    @Query(value = "FROM Topic t where t.project.id = :projectId")
    List<Topic> findAllByProjectId(Long projectId, Pageable pageable);
}
