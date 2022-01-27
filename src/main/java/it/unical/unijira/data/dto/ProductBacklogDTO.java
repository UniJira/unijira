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
    private Long projectId;
    private List<SprintDTO> sprints;
    private List<ProductBacklogInsertionDTO> insertions;
}
