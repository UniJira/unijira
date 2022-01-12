package it.unical.unijira.data.dto.discussionboard;

import it.unical.unijira.data.dto.AbstractBaseDTO;
import it.unical.unijira.data.dto.user.UserInfoDTO;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.discussionboard.Message;
import it.unical.unijira.data.models.projects.Project;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO extends AbstractBaseDTO {

    private Long id;
    private String summary;
    private Long projectId;
    private Long userId;
    private List<MessageDTO> messages;

}
