package it.unical.unijira.services.discussionboard.impl;

import it.unical.unijira.data.dao.discussionboard.TopicRepository;
import it.unical.unijira.data.dao.projects.ProjectRepository;
import it.unical.unijira.data.models.discussionboard.Topic;
import it.unical.unijira.services.discussionboard.TopicService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record TopicServiceImpl(TopicRepository topicRepository, ProjectRepository projectRepository)
        implements TopicService {


    @Override
    public Optional<Topic> save(Topic topic, Long projectId) {
        return Optional.empty();
    }

    @Override
    public Optional<Topic> update(Long id, Topic topic, Long projectId) {
        return Optional.empty();
    }

    @Override
    public void delete(Topic topic) {

    }

    @Override
    public Optional<Topic> findById(Long id, Long projectId) {
        return Optional.empty();
    }

    @Override
    public List<Topic> findAll(Long projectId) {
        return null;
    }
}
