package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.dao.*;
import it.unical.unijira.data.dao.items.ItemRepository;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.*;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemType;
import it.unical.unijira.data.models.projects.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemRetrievesTest extends UniJiraTest {

    @Autowired
    private ProductBacklogRepository backlogRepository;
    @Autowired
    private SprintRepository sprintRepository;
    @Autowired
    private RoadmapRepository roadmapRepository;
    @Autowired
    private ProductBacklogInsertionRepository backlogInsertionRepository;
    @Autowired
    private SprintInsertionRepository sprintInsertionRepository;
    @Autowired
    private RoadmapInsertionRepository roadmapInsertionRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Project savedProject;
    private ProductBacklog savedBacklog;
    private Sprint savedSprint;
    private Roadmap savedRoadmap;

    private final List<Item> savedItems = new ArrayList<>();

    @BeforeEach
    public void initProjectBacklogSprintRoadmap() {
        Project p = new Project();
        p.setOwner(userRepository.findByUsername(UniJiraTest.USERNAME).orElse(null));
        p.setName("DUMMY PROJECT");
        p.setKey("PRV");

        this.savedProject =projectRepository.saveAndFlush(p);

        ProductBacklog pb = new ProductBacklog();
        pb.setProject(p);

        this.savedBacklog = backlogRepository.saveAndFlush(pb);

        Sprint s = new Sprint();
        s.setStartingDate(LocalDate.now());
        s.setEndingDate(LocalDate.of(221,7,25));
        s.setBacklog(this.savedBacklog);

        this.savedSprint = this.sprintRepository.saveAndFlush(s);


        Roadmap roadmap = new Roadmap();
        roadmap.setBacklog(this.savedBacklog);

        this.savedRoadmap = this.roadmapRepository.saveAndFlush(roadmap);



    }

    @BeforeEach
    public void initItemHierarchy() throws NonValidItemTypeException {

        Item epic = new Item();
        epic.setDescription("THIS IS AN EPIC");
        epic.setEvaluation(20000);
        epic.setSummary("EPIC");
        epic.setType(ItemType.EPIC);

        this.savedItems.add(itemRepository.saveAndFlush(epic));

        Item story = new Item();
        story.setDescription("THIS IS A STORY");
        story.setEvaluation(10000);
        story.setSummary("STORY");
        story.setType(ItemType.STORY);
        story.setFather(epic);

        this.savedItems.add(itemRepository.saveAndFlush(story));

        Item task = new Item();
        task.setDescription("THIS IS A TASK");
        task.setEvaluation(500);
        task.setSummary("TASK");
        task.setType(ItemType.TASK);
        task.setFather(story);

        this.savedItems.add(itemRepository.saveAndFlush(task));

        Item secondTask = new Item();
        secondTask.setDescription("THIS IS ANOTHER TASK");
        secondTask.setEvaluation(3000);
        secondTask.setSummary("ANOTHER TASK");
        secondTask.setType(ItemType.TASK);
        secondTask.setFather(epic);

        this.savedItems.add(itemRepository.saveAndFlush(secondTask));

        Item issue = new Item();
        issue.setDescription("THIS IS AN ISSUE");
        issue.setEvaluation(4000);
        issue.setSummary("ISSUE");
        issue.setType(ItemType.ISSUE);
        issue.setFather(task);

        this.savedItems.add(itemRepository.saveAndFlush(issue));

        int i=this.savedItems.size();
        for (Item item : this.savedItems) {
            ProductBacklogInsertion pbInsertion = new ProductBacklogInsertion();
            pbInsertion.setPriority(i);
            pbInsertion.setItem(item);
            pbInsertion.setBacklog(this.savedBacklog);
            this.backlogInsertionRepository.saveAndFlush(pbInsertion);
            i--;

            SprintInsertion sprintInsertion = new SprintInsertion();
            sprintInsertion.setSprint(this.savedSprint);
            sprintInsertion.setItem(item);
            this.sprintInsertionRepository.saveAndFlush(sprintInsertion);

            if(item.getType() == ItemType.EPIC) {
                RoadmapInsertion roadmapInsertion = new RoadmapInsertion();
                roadmapInsertion.setRoadmap(this.savedRoadmap);
                roadmapInsertion.setItem(item);
                roadmapInsertion.setStartingDate(LocalDate.now());
                roadmapInsertion.setEndingDate(LocalDate.now().plusMonths(3));
                this.roadmapInsertionRepository.saveAndFlush(roadmapInsertion);
            }
        }

    }

    @Test
    void readAllItemsOfAProject() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/" + savedProject.getId() + "/items/")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();

        call.andExpect(status().isOk());

        System.out.println(returnValue.getResponse().getContentAsString());

    }

    @Test
    void readAllItemsOfABacklog() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/" + savedProject.getId() +
                "/backlogs/ "+savedBacklog.getId()+"/items/")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();

        call.andExpect(status().isOk());

        System.out.println(returnValue.getResponse().getContentAsString());

    }

    @Test
    void readAllItemsOfASprint() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/" + savedProject.getId() +
                "/backlogs/"+savedBacklog.getId()+"/sprints/"+savedSprint.getId()+"/items")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();

        call.andExpect(status().isOk());

        System.out.println(returnValue.getResponse().getContentAsString());

    }

    @Test
    void readAllItemsOfARoadmap() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/" + savedProject.getId() +
                "/backlogs/"+savedBacklog.getId()+"/roadmaps/"+savedRoadmap.getId()+"/items")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();

        call.andExpect(status().isOk());

        System.out.println(returnValue.getResponse().getContentAsString());

    }

}
