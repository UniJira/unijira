package it.unical.unijira.data.dto.projects;

import it.unical.unijira.data.dto.AbstractBaseDTO;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DefinitionOfDoneEntryDTO extends AbstractBaseDTO {

    private Long id;
    private String description;
    private Integer priority;
    private Long projectId;

}
