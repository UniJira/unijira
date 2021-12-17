package it.unical.unijira.data.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoadmapInsertionDTO {

    private Long id;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private ProductBacklogItemDTO item;
    private RoadmapDTO roadmap;
}
