package it.unical.unijira.data.models.items;

import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.AbstractBaseEntity;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.projects.releases.Release;
import it.unical.unijira.utils.Errors;
import it.unical.unijira.utils.ItemUtils;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Item extends AbstractBaseEntity {

    // SIMPLE FIELDS
    @Id
    @GeneratedValue
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
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private MeasureUnit measureUnit;


    @Column
    @Getter
    @Setter
    private Integer evaluation;


    @Column
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private ItemStatus status;


    // Important to assert for the tags structure
    //Tags are separated by ; and surrounded by ##
    @Column
    @Getter
    @Setter
    private String tags;


    @Column
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private ItemType type;


    // RELATIONSHIPS

    // TODO Add relationship, every productBacklogItem refers to a ProductBacklog
    // Every ProductBacklog refers to a Project


    @OneToMany(mappedBy = "refersTo", cascade = CascadeType.ALL)
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
    @Getter
    @Setter
    @ToString.Exclude
    private List<ItemAssignment> assignees = new ArrayList<>();

    @ManyToOne
    @JoinColumn
    @Getter
    private Item father;

    @OneToMany(mappedBy = "father", cascade = CascadeType.REMOVE)
    @Getter
    @ToString.Exclude
    private List<Item> sons;

    @OneToMany(mappedBy = "key.item", cascade = CascadeType.ALL)
    @Getter
    @ToString.Exclude
    private List<ItemDefinitionOfDone> definitionOfDone;

    @ManyToOne
    @JoinColumn
    @Getter @Setter
    private Release release;


    public void setFather(Item father) throws NonValidItemTypeException{

        if (father == null) return;
        if (!ItemUtils.isValidAssignment(father.getType(), this.type))
            throw new NonValidItemTypeException(String.format(Errors.INVALID_FATHER_ITEM_TYPE,
                    father.getType(), this.getType()));
        this.father  = ItemUtils.isValidAssignment(father.getType(), this.type)
                ? father : null;

    }




}
