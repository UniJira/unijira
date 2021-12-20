package it.unical.unijira.data.dto;

import it.unical.unijira.data.models.Membership;
import it.unical.unijira.data.models.MembershipKey;
import lombok.*;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class MembershipDTO {

    private Long keyUserId;
    private Long keyProjectId;
    private Membership.Role role;
    private Membership.Status status;

}
