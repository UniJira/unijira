package it.unical.unijira.data.dto;

import it.unical.unijira.data.models.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {

    private Long id;
    private String userId;
    private String projectId;
    private Member.Role role;
    private Member.Status status;

}
