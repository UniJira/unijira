package it.unical.unijira.data.dto.user;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SprintDTO {

    private Long id;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private List<SprintInsertionDTO> insertions;
    private ProductBacklogDTO backlog;
}
