package it.unical.unijira.data.dto.projects;

import it.unical.unijira.data.models.projects.releases.ReleaseStatus;
import lombok.*;

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
    private String startDate;
    private String endDate;
    private String createdAt;
    private String updatedAt;

}
