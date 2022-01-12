package it.unical.unijira.data.dto.discussionboard;

import it.unical.unijira.data.dto.AbstractBaseDTO;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.discussionboard.Message;
import it.unical.unijira.data.models.discussionboard.Topic;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO extends AbstractBaseDTO {

    private Long id;
    private String text;
    private Long topicId;
    private Long authorId;
    private String authorUsername;
    private Long repliesToId;
}
