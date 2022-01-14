package it.unical.unijira.data.dto.user;

import it.unical.unijira.data.dto.RoadmapInsertionDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoadmapTreeDTO {

    private RoadmapInsertionDTO content;
    private List<RoadmapTreeDTO> children;
}
