package it.unical.unijira.data.models.projects.releases;

import it.unical.unijira.data.models.AbstractBaseEntity;
import it.unical.unijira.data.models.projects.Project;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class Release extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String version;

    @Column(length = 8192)
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private ReleaseStatus status;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn
    private Project project;


}
