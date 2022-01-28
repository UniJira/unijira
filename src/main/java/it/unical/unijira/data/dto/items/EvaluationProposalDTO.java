package it.unical.unijira.data.dto.items;

import it.unical.unijira.data.dto.AbstractBaseDTO;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationProposalDTO extends AbstractBaseDTO {

    private Long id;
    private Long ticketId;
    private Long authorId;
    private String authorUsername;
    private String authorAvatar;
    private String content;
    private Integer evaluation;

}
