package it.unical.unijira.services.discussionboard.impl;

import it.unical.unijira.data.dao.discussionboard.MessageRepository;
import it.unical.unijira.data.dao.discussionboard.TopicRepository;
import it.unical.unijira.data.models.discussionboard.Message;
import it.unical.unijira.data.models.discussionboard.Topic;
import it.unical.unijira.services.discussionboard.TopicService;
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
                .peek(updated -> updated.setSummary(topic.getSummary())).findFirst().map(topicRepository::saveAndFlush);
    }

    @Override
    public void delete(Topic topic) {

        List<Message> myMessages = messageRepository.findAllByTopicNoPages(topic.getId());
        for (Message m : myMessages) {
            List<Message> myReplies = messageRepository.findMyReplies(m.getId());
            messageRepository.deleteAll(myReplies);
            messageRepository.delete(m);
            //myMessages = messageRepository.findAllByTopicNoPages(topic.getId());
        }
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
