package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@ToString
public class Sprint  extends  AbstractBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Column
    @Basic
    @Getter
    @Setter
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime startingDate = LocalDateTime.now();

    @Column
    @Basic
    @Getter
    @Setter
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime endingDate;


    @OneToMany(mappedBy = "sprint")
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @Setter
    @ToString.Exclude
    private List<SprintInsertion> insertions;


    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private ProductBacklog backlog;



}
