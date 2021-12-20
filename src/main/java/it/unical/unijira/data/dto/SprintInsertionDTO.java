package it.unical.unijira.data.dto;

import it.unical.unijira.data.dto.items.ItemDTO;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SprintInsertionDTO {
    private Long id;
    private Long sprintId;
    private ItemDTO item;
}
