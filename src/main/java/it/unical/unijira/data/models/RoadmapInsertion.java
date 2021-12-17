package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@ToString
public class RoadmapInsertion extends AbstractBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;


    @Column
    @Getter
    @Setter
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime startingDate;


    @Column
    @Getter
    @Setter
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime endingDate;


    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private ProductBacklogItem pbi;


    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private Roadmap roadmap;
}
