package it.unical.unijira.data.dto.user;

import it.unical.unijira.data.dto.RoadmapInsertionDTO;
import it.unical.unijira.data.dto.items.ItemAssignmentDTO;
import it.unical.unijira.data.dto.items.ItemDTO;
import it.unical.unijira.data.dto.items.NoteDTO;
import it.unical.unijira.data.models.items.ItemStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoadmapTreeDTO {

    private Long roadmapInsertionId;
    private LocalDate roadmapInsertionStartingDate;
    private LocalDate roadmapInsertionEndingDate;
    private Long itemId;
    private String itemSummary;
    private String itemDescription;
    private String itemMeasureUnit;
    private Integer itemEvaluation;
    private String itemTags;
    private String itemType;
    private ItemStatus itemStatus;
    private UserInfoDTO itemOwner;
    private Long itemFatherId;
    private List<ItemAssignmentDTO> itemAssignees = new ArrayList<>();
    private List<RoadmapTreeDTO> children;
}
