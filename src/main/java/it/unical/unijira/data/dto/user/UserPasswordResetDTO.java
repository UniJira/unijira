package it.unical.unijira.data.dto.user;

import lombok.*;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class UserPasswordResetDTO {

    private String password;
    private String token;

}
