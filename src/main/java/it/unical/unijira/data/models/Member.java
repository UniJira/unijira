package it.unical.unijira.data.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table
@Getter @Setter @ToString
public class Member extends AbstractBaseEntity {

    public enum Role {
        SCRUM_MASTER,
        PRODUCT_OWNER,
        MEMBER
    }

    public enum Status {
        PENDING,
        ENABLED,
        DISABLED
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private Project project;

    @Column
    @Basic(optional = false)
    private Role role;

    @Column
    @Basic(optional = false)
    private Status status;

}
