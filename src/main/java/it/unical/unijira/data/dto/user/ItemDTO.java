package it.unical.unijira.data.dto.user;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

    private Long id;
    private String summary;
    private String description;
    private String measureUnit;
    private Integer evaluation;
    private String tags;
    private String type;
    private List<NoteDTO> notes = new ArrayList<>();
    private UserInfoDTO owner;
    private List<ItemAssignmentDTO> assignees = new ArrayList<>();
    private ItemDTO father;

}
