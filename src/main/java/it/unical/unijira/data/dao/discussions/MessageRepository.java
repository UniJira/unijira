package it.unical.unijira.data.dao.discussions;

import it.unical.unijira.data.models.discussions.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long>, JpaSpecificationExecutor<Message> {

    @Query("FROM Message m where m.topic.id = :topicId")
    List<Message> findAllByTopic(Long topicId, Pageable pageable);

    @Query("FROM Message m where m.id = :id and m.topic.id = :topicId")
    Optional<Message> findByIdAndTopic(Long id, Long topicId);

    @Query("FROM Message m where m.repliesTo.id = :id")
    List<Message> findMyReplies(Long id);

    @Query("FROM Message m where m.topic.id = :id")
    List<Message> findAllByTopicNoPages(Long id);

    @Query("SELECT COUNT (m) FROM Message m where m.topic.id = :topicId")
    Integer countByTopic(Long topicId);
}
