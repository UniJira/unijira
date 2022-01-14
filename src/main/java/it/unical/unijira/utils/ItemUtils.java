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
        RoadmapTreeDTO toSend = new RoadmapTreeDTO();
        List <RoadmapInsertion> insertionList = roadmapInsertionService.findByItemAndRoadmap(first, roadmap);
        if (insertionList.size() > 0) {
            RoadmapInsertion currentInsertion = insertionList.get(0);
            toSend.setParent(modelMapper.map(currentInsertion, RoadmapInsertionDTO.class));
            List<RoadmapTreeDTO> nextLevel = new ArrayList<>();
            for (Item son : first.getSons()){
                nextLevel.add(manageTree(son,roadmap,roadmapInsertionService,modelMapper));
            }
            toSend.setChildren(nextLevel);

        }
        return toSend;


    }

}
