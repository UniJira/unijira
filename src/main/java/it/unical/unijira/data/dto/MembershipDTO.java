package it.unical.unijira.data.dto;

import it.unical.unijira.data.models.Membership;
import lombok.*;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class MembershipDTO {

    private Long id;
    private String userId;
    private String projectId;
    private Membership.Role role;
    private Membership.Status status;

}
