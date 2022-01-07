package it.unical.unijira.data.dto;

import it.unical.unijira.data.models.projects.Membership;
import lombok.*;

import javax.persistence.Basic;
import javax.persistence.Column;
import java.util.List;
import java.util.Set;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class MembershipDTO {

    private Long keyUserId;
    private Long keyProjectId;
    private Membership.Role role;
    private Membership.Status status;
    private Set<Membership.Permission> permissions;

}
