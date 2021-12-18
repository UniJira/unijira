package it.unical.unijira.utils;

import it.unical.unijira.data.models.*;

public class ControllerUtilities {

    public static boolean checkProjectCoherence(Project project, ProductBacklog backlog) {
        return project != null && backlog != null && project.getId()!= null
                && project.getId().equals(backlog.getProject().getId());
    }

    public static boolean checkProjectValidity(Project project) {
        return project != null;
    }

    public static boolean checkItemCoherence(Project project, ProductBacklog backlog, Item item,
                                             ProductBacklogInsertion insertion) {
        return checkProjectCoherence(project, backlog) && item!= null
                && backlog.getId().equals(insertion.getBacklog().getId())
                && item.getId().equals(insertion.getItem().getId());
    }


    public static boolean checkSprintCoherence(Project project, ProductBacklog backlog, Sprint sprint) {
        return checkProjectCoherence(project, backlog) && sprint!= null
                && backlog.getId().equals(sprint.getBacklog().getId());
    }

    public static boolean checkItemCoherenceInSprint(Project projectObj, ProductBacklog backlogObj, Sprint sprintObj,
                                                     SprintInsertion sprintInsertionObj) {

        return checkSprintCoherence(projectObj,backlogObj,sprintObj) && sprintInsertionObj!= null &&
                sprintInsertionObj.getSprint() != null && sprintInsertionObj.getSprint().getId() != null &&
                sprintInsertionObj.getSprint().getId().equals(sprintObj.getId());
    }
}
