package it.unical.unijira.services.discussions.impl;

import it.unical.unijira.data.dao.discussions.MessageRepository;
import it.unical.unijira.data.dao.discussions.TopicRepository;
import it.unical.unijira.data.models.discussions.Topic;
import it.unical.unijira.services.discussions.TopicService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record TopicServiceImpl(TopicRepository topicRepository, MessageRepository messageRepository)
        implements TopicService {


    @Override
    public Optional<Topic> save(Topic topic) {
        return Optional.of(topicRepository.saveAndFlush(topic));
    }

    @Override
    public Optional<Topic> update(Long id, Topic topic, Long projectId) {
        return topicRepository.findByIdAndProject(id,projectId).stream()
                .peek(updated -> {
                    updated.setTitle(topic.getTitle());
                    updated.setContent(topic.getContent());
                    updated.setType(topic.getType());
                }).findFirst().map(topicRepository::saveAndFlush);
    }

    @Override
    public void delete(Topic topic) {
        topicRepository.delete(topic);
    }



    @Override
    public Optional<Topic> findById(Long id, Long projectId) {
        return topicRepository.findByIdAndProject(id,projectId);
    }

    @Override
    public List<Topic> findAll(Long projectId, int page, int size) {
        return topicRepository.findAllByProjectId(projectId, PageRequest.of(page,size));
    }
}
