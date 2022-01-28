package it.unical.unijira.data.dto.user;

import it.unical.unijira.data.models.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class UserInfoDTO {

    private Long id;
    private String username;
    private User.Status status;
    private boolean disabled;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate birthDate;
    private String firstName;
    private String lastName;
    private String role;
    private String description;
    private String github;
    private String linkedin;
    private String phoneNumber;

}
