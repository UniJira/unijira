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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ProductBacklogItem {

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
    private LocalDateTime createdAt = LocalDateTime.now();


    @Column
    @Basic
    @Getter
    @Setter
    private LocalDateTime updatedAt = LocalDateTime.now();


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

    // TODO Add relationship, every productBacklogItem refers to a ProductBacklog
    // Every ProductBacklog refers to a Project


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




}
