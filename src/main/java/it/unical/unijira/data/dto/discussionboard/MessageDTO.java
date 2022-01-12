package it.unical.unijira.data.dto.discussionboard;

import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.discussionboard.Message;
import it.unical.unijira.data.models.discussionboard.Topic;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    private String text;
    private Long topicId;
    private Long authorId;
    private String authorUsername;
    private MessageDTO repliesTo;
}
