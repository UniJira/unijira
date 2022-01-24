package it.unical.unijira.data.models.projects;

import it.unical.unijira.data.models.AbstractBaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class Membership extends AbstractBaseEntity {

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

    public enum Permission {
        ADMIN,
        DETAILS,
        ROLES,
        INVITATIONS
    }

    @EmbeddedId
    private MembershipKey key;

    @Column
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private Role role;

    @Column
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private Status status;

    @ElementCollection(targetClass = Permission.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable
    @ToString.Exclude
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

}
