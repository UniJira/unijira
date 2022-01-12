package it.unical.unijira.data.dto.discussionboard;

import it.unical.unijira.data.dto.AbstractBaseDTO;

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
