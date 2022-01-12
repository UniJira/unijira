package it.unical.unijira.data.dto.discussions;

import it.unical.unijira.data.dto.AbstractBaseDTO;
import lombok.*;


@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO extends AbstractBaseDTO {

    private Long id;
    private String content;
    private Long topicId;
    private Long authorId;
    private String authorUsername;
    private Long repliesToId;
}
