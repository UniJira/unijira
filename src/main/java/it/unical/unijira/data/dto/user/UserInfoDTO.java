package it.unical.unijira.data.dto.user;

import lombok.*;

import javax.persistence.Column;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class UserInfoDTO {

    private Long id;
    private String username;
    private boolean activated;
    private boolean disabled;
    private URL avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate birthDate;
    private String firstName;
    private String lastName;
    private String role;
    private String description;

}
