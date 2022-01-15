package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.RoadmapInsertion;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemStatus;
import it.unical.unijira.data.models.items.ItemType;
import it.unical.unijira.data.models.items.MeasureUnit;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.common.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class RoadmapTreeApiTest extends UniJiraTest {


    private Roadmap savedRoadmap;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProductBacklogService backlogService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private RoadmapInsertionService roadmapInsertionService;

    @BeforeEach
    void initAllStuff() {

        Project project = Project.builder()
                .name("DUMMY PROJECT")
                .key("KEY")
                .owner(userRepository.findByUsername(UniJiraTest.USERNAME).orElse(null)).build();

        Project savedProject =projectService.save(project).orElse(null);



        ProductBacklog backlog = ProductBacklog.builder()
                .project(savedProject).build();

        ProductBacklog savedBacklog = backlogService.save(backlog).orElse(null);

        Roadmap roadmap = Roadmap.builder()
                .backlog(savedBacklog).build();

        this.savedRoadmap = roadmapService.save(roadmap).orElse(null);

        Item forTests = Item.builder()
                .summary("As a test item i want to test myself so that i can be tested")
                .description("This item is a test item which has to be tested. Is it clear?")
                .measureUnit(MeasureUnit.STORY_POINTS)
                .type(ItemType.EPIC)
                .evaluation(7)
                .status(ItemStatus.OPEN)
                .build();

        Item savedEpic = itemService.save(forTests).orElse(null);

        RoadmapInsertion insertion = new RoadmapInsertion();
        insertion.setRoadmap(this.savedRoadmap);
        insertion.setItem(savedEpic);
        insertion.setStartingDate(LocalDate.now());
        insertion.setEndingDate(LocalDate.now().plusMonths(2));

        roadmapInsertionService.save(insertion);

        forTests = Item.builder()
                .summary("As a test item i want to test myself so that i can be tested")
                .description("This item is a test item which has to be tested. Is it clear?")
                .measureUnit(MeasureUnit.WORKING_DAYS)
                .type(ItemType.STORY)
                .evaluation(7)
                .status(ItemStatus.OPEN)
                .father(savedEpic)
                .build();

        Item savedStory = itemService.save(forTests).orElse(null);

        insertion = new RoadmapInsertion();
        insertion.setRoadmap(this.savedRoadmap);
        insertion.setItem(savedStory);
        insertion.setStartingDate(LocalDate.now());
        insertion.setEndingDate(LocalDate.now().plusMonths(1));

        roadmapInsertionService.save(insertion);


        forTests = Item.builder()
                .summary("As a test item i want to test myself so that i can be tested")
                .description("This item is a test item which has to be tested. Is it clear?")
                .measureUnit(MeasureUnit.WORKING_HOURS)
                .type(ItemType.TASK)
                .evaluation(5)
                .status(ItemStatus.OPEN)
                .father(savedStory)
                .build();

        Item savedTask = itemService.save(forTests).orElse(null);

        insertion = new RoadmapInsertion();
        insertion.setRoadmap(this.savedRoadmap);
        insertion.setItem(savedTask);
        insertion.setStartingDate(LocalDate.now());
        insertion.setEndingDate(LocalDate.now().plusMonths(19));

        roadmapInsertionService.save(insertion);


        forTests = Item.builder()
                .summary("As a test item i want to test myself so that i can be tested")
                .description("This item is a test item which has to be tested. Is it clear?")
                .measureUnit(MeasureUnit.WORKING_HOURS)
                .type(ItemType.TASK)
                .evaluation(1995)
                .status(ItemStatus.OPEN)
                .father(savedEpic)
                .build();

        Item anotherSavedTask = itemService.save(forTests).orElse(null);

        insertion = new RoadmapInsertion();
        insertion.setRoadmap(this.savedRoadmap);
        insertion.setItem(anotherSavedTask);
        insertion.setStartingDate(LocalDate.now().minusMonths(1));
        insertion.setEndingDate(LocalDate.now());

        roadmapInsertionService.save(insertion);

    }

    @Test
    void testRoadmapTreeApi() throws Exception{
        ResultActions call = mockMvc.perform(get("/projects/"
                +savedRoadmap.getBacklog().getProject().getId()
                +"/backlogs/"+savedRoadmap.getBacklog().getId()
                +"/roadmaps/"+savedRoadmap.getId()+"/tree")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));
        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());
    }

}
