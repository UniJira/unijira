package it.unical.unijira.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/*
@Component
@ApplicationScope
 */
public class ProductBacklogItemType {


    public final String EPIC = "epic";

    public final String STORY = "story";

    public final String TASK = "task";

    public final String ISSUE = "issue";


    // key is father, values are available sons
    private final HashMap<String, List<String>> fatherAndSon;

    private static ProductBacklogItemType instance;

    private ProductBacklogItemType(){
        this.fatherAndSon = new HashMap<>();
        this.fatherAndSon.put(this.EPIC, Arrays.asList(this.STORY, this.TASK));
        this.fatherAndSon.put(this.STORY, Arrays.asList(this.TASK, this.ISSUE));
        this.fatherAndSon.put(this.TASK, Arrays.asList(this.TASK, this.ISSUE));
        this.fatherAndSon.put(this.ISSUE, Collections.emptyList());

    }

    public static ProductBacklogItemType getInstance(){

        if (instance == null){
            instance = new ProductBacklogItemType();
        }
        return instance;

    }

    public boolean isValidAssignment(String father, String son) {
        if (null == father) return false;
        List<String> sons = this.fatherAndSon.get(father);
        return sons.contains(son);

    }

    public boolean isCoherentType(String type){
        return (this.ISSUE.equals(type) || this.TASK.equals(type) || this.STORY.equals(type) || this.EPIC.equals(type));
    }

}
