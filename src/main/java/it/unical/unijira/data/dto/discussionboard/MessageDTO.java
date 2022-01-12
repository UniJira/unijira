package it.unical.unijira.data.dto.discussionboard;

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
    private String text;
    private Long topicId;
    private Long authorId;
    private String authorUsername;
    private Long repliesToId;
}
