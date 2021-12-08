package it.unical.unijira.data.dto.user;


import lombok.*;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class UserAuthenticationDTO {

    private String username;
    private String password;

}
