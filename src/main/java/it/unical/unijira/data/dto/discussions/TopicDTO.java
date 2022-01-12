package it.unical.unijira.data.dto.discussions;

import it.unical.unijira.data.dto.AbstractBaseDTO;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO extends AbstractBaseDTO {

    private Long id;
    private String title;
    private String content;
    private Long projectId;
    private Long authorId;

}
