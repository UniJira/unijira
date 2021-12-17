package it.unical.unijira.data.models;

import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.utils.Errors;
import it.unical.unijira.utils.ProductBacklogItemType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@ToString
public class ProductBacklogItem extends AbstractBaseEntity{

    // SIMPLE FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;


    @Column
    @Basic(optional = false)
    @Getter
    @Setter
    private String summary;


    @Column
    @Basic(optional = false)
    @Getter
    @Setter
    private String description;


    @Column
    @Getter
    @Setter
    private String measureUnit;


    @Column
    @Getter
    @Setter
    private Integer evaluation;


    // Important to assert for the tags structure
    //Tags are separated by ; and surrounded by ##
    @Column
    @Getter
    @Setter
    private String tags;


    @Column
    @Basic(optional = false)
    @Getter
    private String type;

    public void setType(String type) throws NonValidItemTypeException {
        if (ProductBacklogItemType.getInstance().isCoherentType(type)) {
            this.type = type;
        } else {
            throw new NonValidItemTypeException(String.format(Errors.INVALID_BACKLOG_ITEM_TYPE,type));
        }
    }


    // RELATIONSHIPS




    @OneToMany(mappedBy = "refersTo", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @Setter
    @ToString.Exclude
    private List<Note> notes = new ArrayList<>();


    //who creates the Item
    @ManyToOne
    @JoinColumn
    @Getter
    @Setter
    private User owner;


    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @Setter
    @ToString.Exclude
    private List<ItemAssignment> assignees = new ArrayList<>();

    @ManyToOne
    @JoinColumn
    @Getter
    private ProductBacklogItem father;

    @OneToMany(mappedBy = "father", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @ToString.Exclude
    private List<ProductBacklogItem> sons;

    public void setFather(ProductBacklogItem father) throws NonValidItemTypeException{

        if (father == null) return;

        if (!ProductBacklogItemType.getInstance().isValidAssignment(father.getType(), this.type))
            throw new NonValidItemTypeException(String.format(Errors.INVALID_FATHER_ITEM_TYPE,
                    father.getType(), this.getType()));
        this.father  = ProductBacklogItemType.getInstance().isValidAssignment(father.getType(), this.type)
                ? father : null;

    }

    //This is a list but an item can stay just in one backlog.
    //but the insertion has its own fields, so i decided to keep the tables separated

    @OneToOne(mappedBy = "item", cascade = CascadeType.REMOVE)
    @Getter
    @Setter
    @ToString.Exclude
    private ProductBacklogInsertion backlogImIn;


    @OneToMany(mappedBy = "pbi")
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @Setter
    @ToString.Exclude
    private List<SprintInsertion> sprintsImIn;


    @OneToMany(mappedBy = "pbi")
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @Setter
    @ToString.Exclude
    private List<RoadmapInsertion> roadmapsImIn;



}
