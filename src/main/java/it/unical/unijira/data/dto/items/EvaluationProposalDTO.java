package it.unical.unijira.data.dto.items;

import it.unical.unijira.data.dto.AbstractBaseDTO;
import lombok.*;

import java.net.URL;

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
    private URL authorAvatar;
    private String content;
    private Integer evaluation;

}
