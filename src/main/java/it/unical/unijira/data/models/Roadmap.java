package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@ToString
public class Roadmap extends AbstractBaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;


    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private ProductBacklog backlog;


    @OneToMany(mappedBy = "roadmap" )
    @LazyCollection(LazyCollectionOption.FALSE)
    @Setter
    @Getter
    @ToString.Exclude
    private List<RoadmapInsertion> insertions;

}
