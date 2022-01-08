package it.unical.unijira.data.dto.items;
import it.unical.unijira.data.dto.user.UserInfoDTO;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemAssignmentDTO {

    private Long id;
    private Long itemId;
    private UserInfoDTO assignee;
}
