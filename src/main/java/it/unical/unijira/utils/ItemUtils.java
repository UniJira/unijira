package it.unical.unijira.utils;

import it.unical.unijira.data.dto.RoadmapInsertionDTO;
import it.unical.unijira.data.dto.user.RoadmapTreeDTO;
import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.RoadmapInsertion;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemType;
import it.unical.unijira.services.common.RoadmapInsertionService;
import org.modelmapper.ModelMapper;

import java.util.*;

/*
@Component
@ApplicationScope
 */
public class ItemUtils {





    // key is father, values are available sons
    private static final HashMap<ItemType, List<ItemType>> validAssignments;

    static {
        validAssignments = new HashMap<>();
        validAssignments.put(ItemType.EPIC, Arrays.asList(ItemType.STORY, ItemType.TASK, ItemType.ISSUE));
        validAssignments.put(ItemType.STORY, Arrays.asList(ItemType.ISSUE, ItemType.TASK));
        validAssignments.put(ItemType.TASK, Arrays.asList(ItemType.ISSUE));
        validAssignments.put(ItemType.ISSUE, Collections.emptyList());
    }


    public static boolean isValidAssignment(ItemType father, ItemType son) {
        if (null == father || null == son) return false;
        List<ItemType> sons = validAssignments.get(father);
        return sons.contains(son);

    }


    public static RoadmapTreeDTO manageTree(Item first, Roadmap roadmap,
                                            RoadmapInsertionService roadmapInsertionService, ModelMapper modelMapper) {
        RoadmapTreeDTO toSend = null;
        List <RoadmapInsertion> insertionList = roadmapInsertionService.findByItemAndRoadmap(first, roadmap);
        for (RoadmapInsertion insertion : insertionList) {
            if (insertion != null) {
                RoadmapInsertion currentInsertion = insertionList.get(0);
                RoadmapInsertionDTO dto = modelMapper.map(currentInsertion, RoadmapInsertionDTO.class);
                toSend = RoadmapTreeDTO.builder()
                        .roadmapInsertionId(dto.getId())
                        .roadmapInsertionStartingDate(dto.getStartingDate())
                        .roadmapInsertionEndingDate(dto.getEndingDate())
                        .itemAssignees(dto.getItem().getAssignees())
                        .itemType(dto.getItem().getType())
                        .itemTags(dto.getItem().getTags())
                        .itemSummary(dto.getItem().getSummary())
                        .itemStatus(dto.getItem().getStatus())
                        .itemMeasureUnit(dto.getItem().getMeasureUnit())
                        .itemOwner(dto.getItem().getOwner())
                        .itemEvaluation(dto.getItem().getEvaluation())
                        .itemDescription(dto.getItem().getDescription())
                        .itemId(dto.getItem().getId())
                        .itemFatherId(dto.getItem().getFatherId()).build();

                List<RoadmapTreeDTO> nextLevel = new ArrayList<>();
                for (Item son : first.getSons()) {
                    RoadmapTreeDTO toAdd = manageTree(son, roadmap, roadmapInsertionService, modelMapper);
                    if(toAdd!=null)
                    nextLevel.add(toAdd);
                }
                if(!(nextLevel == null || nextLevel.isEmpty())) {
                    toSend.setChildren(nextLevel);
                }
            }
        }
        return toSend;


    }

}
