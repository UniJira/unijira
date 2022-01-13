package it.unical.unijira.services.discussionboard.impl;

import it.unical.unijira.data.dao.discussionboard.MessageRepository;
import it.unical.unijira.data.models.discussionboard.Message;
import it.unical.unijira.services.discussionboard.MessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public record MessageServiceImpl (MessageRepository messageRepository)
        implements MessageService {

    @Override
    public Optional<Message> save(Message message) {
        return Optional.of(messageRepository.saveAndFlush(message));
    }

    @Override
    public Optional<Message> update(Long id, Message message, Long projectId, Long topicId) {
        return messageRepository.findByIdAndTopic(id,topicId).stream()
                .peek(updated -> updated.setText(message.getText())).findFirst().map(messageRepository::saveAndFlush);
    }

    @Override
    public void delete(Message message, Long projectId, Long topicId) {
        List<Message> myReplies = messageRepository.findMyReplies(message.getId());
        messageRepository.deleteAll(myReplies);

    }

    @Override
    public Optional<Message> findById(Long id, Long topicId) {
        return messageRepository.findByIdAndTopic(id,topicId);
    }

    @Override
    public List<Message> findAll(Long topicId, int page, int size) {
        return messageRepository.findAllByTopic(topicId, PageRequest.of(page,size));
    }

    @Override
    public Integer countByTopic(Long topicId) {
        return messageRepository.countByTopic(topicId);
    }
}
