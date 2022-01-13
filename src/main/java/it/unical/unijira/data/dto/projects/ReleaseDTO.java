package it.unical.unijira.data.dto.projects;

import it.unical.unijira.data.models.projects.releases.ReleaseStatus;
import lombok.*;

import java.time.LocalDate;

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
    private LocalDate startDate;
    private LocalDate endDate;
    private String createdAt;
    private String updatedAt;

}
