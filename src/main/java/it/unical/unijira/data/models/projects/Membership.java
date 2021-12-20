package it.unical.unijira.data.models.projects;

import it.unical.unijira.data.models.AbstractBaseEntity;
import lombok.*;

import javax.persistence.*;

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

    @EmbeddedId
    private MembershipKey key;

    @Column
    @Basic(optional = false)
    private Role role;

    @Column
    @Basic(optional = false)
    private Status status;

}
