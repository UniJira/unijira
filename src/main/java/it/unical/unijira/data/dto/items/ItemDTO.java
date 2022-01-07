package it.unical.unijira.data.dto.items;

import it.unical.unijira.data.dto.AbstractBaseDTO;
import it.unical.unijira.data.dto.user.UserInfoDTO;
import it.unical.unijira.data.models.items.ItemStatus;
import lombok.*;

import java.util.ArrayList;
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
    private UserInfoDTO owner;
    private Long fatherId;
    private List<ItemDTO> sons;
    private List<NoteDTO> notes = new ArrayList<>();
    private List<ItemAssignmentDTO> assignees = new ArrayList<>();

}
