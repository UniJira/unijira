package it.unical.unijira.services.discussionboard;


import it.unical.unijira.data.models.discussionboard.Message;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    Optional<Message> save (Message message, Long projectId, Long topicId);
    Optional<Message> update (Long id, Message message, Long projectId, Long topicId);
    void delete(Message message, Long projectId, Long topicId);
    Optional<Message> findById(Long id, Long projectId, Long topicId);
    List<Message> findAll(Long projectId, Long topicId);
}
