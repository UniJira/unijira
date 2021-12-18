package it.unical.unijira.data.dto.user;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductBacklogInsertionDTO {

    private Long id;
    private ItemDTO item;
    private ProductBacklogDTO backlog;
    private Integer priority;
}
