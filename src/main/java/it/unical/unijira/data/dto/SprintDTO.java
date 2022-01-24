package it.unical.unijira.data.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SprintDTO {

    private Long id;
    private String startingDate;
    private String endingDate;
    private List<SprintInsertionDTO> insertions;
    private Long backlogId;

}
