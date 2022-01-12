package it.unical.unijira.data.dto;

import it.unical.unijira.data.dto.items.ItemDTO;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoadmapInsertionDTO {

    private Long id;
    private String startingDate;
    private String endingDate;
    private ItemDTO item;
    private Long roadmapId;

}
