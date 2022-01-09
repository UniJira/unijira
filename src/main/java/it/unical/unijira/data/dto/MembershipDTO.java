package it.unical.unijira.data.dto;

import it.unical.unijira.data.models.projects.Membership;
import lombok.*;

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
