package it.unical.unijira.data.dto.user;

import lombok.*;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class UserExpiredTokenDTO {

    private String token;

}
