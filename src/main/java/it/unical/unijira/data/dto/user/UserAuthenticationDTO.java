package it.unical.unijira.data.dto.user;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserAuthenticationDTO {

    private String username;
    private String password;

}
