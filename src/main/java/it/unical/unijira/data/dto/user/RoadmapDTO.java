package it.unical.unijira.data.dto.user;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoadmapDTO {


    private Long id;
    private ProductBacklogDTO backlog;
    private List<RoadmapInsertionDTO> insertions;
}
