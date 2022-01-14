package it.unical.unijira.data.models;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Roadmap extends AbstractBaseEntity {


    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;


    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private ProductBacklog backlog;


    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Setter
    @Getter
    @ToString.Exclude
    private List<RoadmapInsertion> insertions;

}
