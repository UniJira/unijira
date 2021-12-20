package it.unical.unijira.data.dto.user;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemAssignmentDTO {

    private Long id;
    private ItemDTO item;
    private UserInfoDTO assignee;
}
