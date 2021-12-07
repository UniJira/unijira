package it.unical.unijira.data.dto.user;

import lombok.*;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class UserRegisterDTO {

    private String username;
    private String password;

    /* TODO: Fill with other user data fields */

}
