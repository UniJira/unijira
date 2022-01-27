package it.unical.unijira.data.dto.items;

import it.unical.unijira.data.dto.AbstractBaseDTO;
import it.unical.unijira.data.dto.user.UserInfoDTO;
import it.unical.unijira.data.models.items.ItemStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO extends AbstractBaseDTO {

    private Long id;
    private String summary;
    private String description;
    private String measureUnit;
    private Integer evaluation;
    private String tags;
    private String type;
    private ItemStatus status;
    private LocalDate doneOn;
    private UserInfoDTO owner;
    private Long releaseId;
    private String releaseVersion;
    private Long fatherId;
    private Long projectId;
    private List<ItemDTO> sons;
    private List<NoteDTO> notes;
    private List<ItemAssignmentDTO> assignees;

}
