package it.unical.unijira.data.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductBacklogDTO {

    private Long id;
    private ProjectDTO project;
    private List<SprintDTO> sprints;
    private List<ProductBacklogInsertionDTO> insertions;
}
