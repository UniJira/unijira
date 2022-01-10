package it.unical.unijira.data.dto.user;

import it.unical.unijira.data.models.User;
import lombok.*;

import java.net.URL;
import java.time.LocalDateTime;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class UserInfoDTO {

    private Long id;
    private String username;
    private User.Status status;
    private boolean disabled;
    private URL avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
