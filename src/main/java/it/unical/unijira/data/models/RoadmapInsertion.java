package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
@ToString
public class RoadmapInsertion extends AbstractBaseEntity{

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;


    @Column
    @Getter
    @Setter
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startingDate;


    @Column
    @Getter
    @Setter
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endingDate;


    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private Item item;


    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private Roadmap roadmap;
}
