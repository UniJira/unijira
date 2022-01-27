package it.unical.unijira.data.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class InviteMembersDTO {
    private Long projectId;
    private List<String> emails;
}
