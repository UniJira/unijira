package it.unical.unijira.data.dto.items;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemAssignmentDTO {

    private Long id;
    private Long itemId;
    private Long assigneeId;
}
