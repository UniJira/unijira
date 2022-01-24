package it.unical.unijira.data.dto.items;

import lombok.*;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class ItemDefinitionOfDoneDTO {
    private Long keyDefinitionOfDoneEntryId;
    private Long keyItemId;
}
