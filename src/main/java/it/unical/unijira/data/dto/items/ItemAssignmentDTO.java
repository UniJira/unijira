package it.unical.unijira.data.dto.items;

import lombok.*;

import java.net.URL;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemAssignmentDTO {

    private Long id;
    private Long itemId;
    private Long assigneeId;
    private String assigneeUsername;
    private String assigneeAvatar;

}
