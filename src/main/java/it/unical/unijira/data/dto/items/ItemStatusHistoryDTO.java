package it.unical.unijira.data.dto.items;

import it.unical.unijira.data.models.items.ItemStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemStatusHistoryDTO {
    private Long id;
    private Long itemId;
    private ItemStatus oldStatus;
    private ItemStatus newStatus;
    private LocalDateTime changeDate;
}
