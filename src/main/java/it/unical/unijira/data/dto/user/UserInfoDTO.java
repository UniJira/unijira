package it.unical.unijira.data.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfoDTO {

    private Long id;
    private String username;
    private boolean activated;
    private boolean disabled;

}
