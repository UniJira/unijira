package it.unical.unijira.services.discussionboard.impl;

import it.unical.unijira.data.dao.discussionboard.MessageRepository;
import it.unical.unijira.data.dao.discussionboard.TopicRepository;
import it.unical.unijira.data.dao.projects.ProjectRepository;
import it.unical.unijira.data.models.discussionboard.Message;
import it.unical.unijira.services.discussionboard.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record MessageServiceImpl (MessageRepository messageRepository,
                                  TopicRepository topicRepository,
                                  ProjectRepository projectRepository) implements MessageService {

    @Override
    public Optional<Message> save(Message message, Long projectId, Long topicId) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Long id, Message message, Long projectId, Long topicId) {
        return Optional.empty();
    }

    @Override
    public void delete(Message message, Long projectId, Long topicId) {

    }

    @Override
    public Optional<Message> findById(Long id, Long projectId, Long topicId) {
        return Optional.empty();
    }

    @Override
    public List<Message> findAll(Long projectId, Long topicId) {
        return null;
    }
}
