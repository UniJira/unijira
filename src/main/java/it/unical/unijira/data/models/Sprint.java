package it.unical.unijira.data.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sprint  extends  AbstractBaseEntity{

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Column
    @Basic
    @Getter
    @Setter
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate startingDate;

    @Column
    @Basic
    @Getter
    @Setter
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate endingDate;


    @OneToMany(mappedBy = "sprint", cascade = CascadeType.REMOVE)
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

    @Column
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private SprintStatus status;



}
