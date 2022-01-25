package it.unical.unijira.data.dto.discussions;

import it.unical.unijira.data.dto.AbstractBaseDTO;
import it.unical.unijira.data.models.discussions.TopicType;
import lombok.*;

import java.net.URL;


@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class TopicDTO extends AbstractBaseDTO {

    private Long id;
    private String title;
    private String content;
    private Long projectId;
    private Long userId;
    private String userUsername;
    private URL userAvatar;
    private TopicType type;

}
