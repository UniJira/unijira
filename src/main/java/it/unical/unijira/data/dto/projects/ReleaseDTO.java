package it.unical.unijira.data.dto.projects;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.unical.unijira.data.models.projects.releases.ReleaseStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseDTO {

    private Long id;
    private String version;
    private String description;
    private ReleaseStatus status;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;

}
