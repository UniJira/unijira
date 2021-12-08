package it.unical.unijira.data.dto.user;

import it.unical.unijira.data.models.ProductBacklogItem;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NoteDTO {

    private Long id;
    private LocalDateTime timestamp;
    private String message;
    private NoteDTO replyTo;
    private ProductBacklogItemDTO refersTo;
    private UserInfoDTO author;
}
