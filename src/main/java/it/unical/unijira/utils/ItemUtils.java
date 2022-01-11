package it.unical.unijira.utils;

import it.unical.unijira.data.models.items.ItemType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/*
@Component
@ApplicationScope
 */
public class ItemUtils {





    // key is father, values are available sons
    private static HashMap<ItemType, List<ItemType>> validAssignments;

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

}
