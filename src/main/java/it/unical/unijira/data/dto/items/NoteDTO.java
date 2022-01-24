package it.unical.unijira.data.dto.items;

import it.unical.unijira.data.dto.user.UserInfoDTO;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NoteDTO {

    private Long id;
    private String message;
    private NoteDTO replyTo;
    private ItemDTO refersTo;
    private UserInfoDTO author;
    private String createdAt;
    private String updatedAt;
}
