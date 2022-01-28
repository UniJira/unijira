package it.unical.unijira.data.dto;

import it.unical.unijira.data.models.SprintStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SprintDTO {

    private Long id;
    private LocalDate startingDate;
    private LocalDate endingDate;
    private List<SprintInsertionDTO> insertions;
    private Long backlogId;
    private SprintStatus status;

}
