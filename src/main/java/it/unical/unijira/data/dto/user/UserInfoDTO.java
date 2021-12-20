package it.unical.unijira.data.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class UserInfoDTO {

    private Long id;
    private String username;
    private boolean activated;
    private boolean disabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
