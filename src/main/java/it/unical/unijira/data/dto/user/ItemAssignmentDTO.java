package it.unical.unijira.data.dto.user;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemAssignmentDTO {

    private Long id;
    private ProductBacklogItemDTO item;
    private UserInfoDTO assignee;
}
