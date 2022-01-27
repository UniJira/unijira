package it.unical.unijira.data.models.projects.releases;

import it.unical.unijira.data.models.AbstractBaseEntity;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.projects.Project;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn
    private Project project;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();


}
