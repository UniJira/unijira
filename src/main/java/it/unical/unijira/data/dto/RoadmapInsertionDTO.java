package it.unical.unijira.data.dto;

import it.unical.unijira.data.dto.items.ItemDTO;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoadmapInsertionDTO {

    private Long id;
    private LocalDate startingDate;
    private LocalDate endingDate;
    private ItemDTO item;
    private Long roadmapId;

}
