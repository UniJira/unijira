package it.unical.unijira.controllers.discussionboard;

import it.unical.unijira.controllers.common.CrudController;
import it.unical.unijira.data.dto.discussionboard.MessageDTO;
import it.unical.unijira.data.dto.discussionboard.TopicDTO;
import it.unical.unijira.data.models.discussionboard.Message;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/topics/")
//Not overridingCrudController because of the {projectId} in the main path
public class TopicController {

    @GetMapping("")
    public ResponseEntity<List<TopicDTO>> readAll(ModelMapper modelMapper,
                                                  @RequestParam (required = false, defaultValue = "0") Integer page,
                                                  @RequestParam (required = false, defaultValue = "10000") Integer size,
                                                  @PathVariable Long projectId) {
        return null;
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<TopicDTO> read(ModelMapper modelMapper, Long id,
                                         @PathVariable Long projectId) {
        return null;
    }

    @PostMapping("")
    public ResponseEntity<TopicDTO> create(ModelMapper modelMapper, TopicDTO dto,
                                           @PathVariable Long projectId) {
        return null;
    }

    @PutMapping("/{topicId}")
    public ResponseEntity<TopicDTO> update(ModelMapper modelMapper, Long id, TopicDTO dto,
                                           @PathVariable Long projectId) {
        return null;
    }

    @DeleteMapping("/{topicId}")
    public ResponseEntity<Boolean> delete(Long id,
                                          @PathVariable Long projectId) {
        return null;
    }



    @GetMapping("/{topicId}/messages")
    public ResponseEntity<List<MessageDTO>> readAllMessages(ModelMapper modelMapper,
                                                            @RequestParam (required = false, defaultValue = "0") Integer page,
                                                            @RequestParam (required = false, defaultValue = "10000") Integer size,
                                                            @PathVariable Long projectId,
                                                            @PathVariable Long topicId) {
        return null;
    }

    @GetMapping("/{topicId}/messages/{messageId}")
    public ResponseEntity<MessageDTO> readMessage(ModelMapper modelMapper, Long id,
                                                  @PathVariable Long projectId, @PathVariable Long topicId) {
        return null;
    }

    @PostMapping("/{topicId}/messages/{messageId}")
    public ResponseEntity<MessageDTO> createMessage(ModelMapper modelMapper, MessageDTO dto,
                                                    @PathVariable Long projectId, @PathVariable Long topicId) {
        return null;
    }

    @PutMapping("/{topicId}/messages/{messageId}")
    public ResponseEntity<MessageDTO> updateMessage(ModelMapper modelMapper, Long id, MessageDTO dto,
                                                    @PathVariable Long projectId, @PathVariable Long topicId) {
        return null;
    }

    @DeleteMapping("/{topicId}/messages/{messageId}")
    public ResponseEntity<Boolean> deleteMessage(Long id,
                                                 @PathVariable Long projectId, @PathVariable Long topicId) {
        return null;
    }

}
