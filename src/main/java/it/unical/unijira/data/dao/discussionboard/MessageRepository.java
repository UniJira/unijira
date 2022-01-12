package it.unical.unijira.data.dao.discussionboard;

import it.unical.unijira.data.models.discussionboard.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long>, JpaSpecificationExecutor<Message> {
}
