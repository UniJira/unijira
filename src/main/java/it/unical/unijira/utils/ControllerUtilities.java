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

    public static boolean checkItemCoherenceInSprint(Project project, ProductBacklog backlog, Sprint sprint,
                                                     SprintInsertion sprintInsertion) {

        return checkSprintCoherence(project,backlog,sprint) && sprintInsertion!= null &&
                sprintInsertion.getSprint() != null && sprintInsertion.getSprint().getId() != null &&
                sprintInsertion.getSprint().getId().equals(sprint.getId());
    }

    public static boolean checkRoadmapCoherence(Project project, ProductBacklog backlog, Roadmap roadmap) {

        return checkProjectCoherence(project, backlog) && roadmap!= null
                && backlog.getId().equals(roadmap.getBacklog().getId());
    }

    public static boolean checkItemCoherenceInRoadmap(Project project, ProductBacklog backlog, Roadmap roadmap,
                                                      RoadmapInsertion roadmapInsertion) {

        return checkRoadmapCoherence(project,backlog,roadmap) && roadmapInsertion!= null &&
                roadmapInsertion.getRoadmap() != null && roadmapInsertion.getRoadmap().getId() != null &&
                roadmapInsertion.getRoadmap().getId().equals(roadmap.getId());
    }
}
