package it.unical.unijira.data.dto;

import lombok.*;

import java.net.URL;
import java.util.List;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class ProjectDTO extends AbstractBaseDTO {

    private Long id;
    private String name;
    private String key;
    private URL icon;
    private Long ownerId;
    private List<ProductBacklogDTO> backlogs;

}
