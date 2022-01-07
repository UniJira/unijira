package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.models.*;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.common.*;
import it.unical.unijira.utils.ItemType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BacklogControllerTest extends UniJiraTest {

    private Project projectForTests;
    private String projectJsonForTests;
    private ProductBacklog backlogForTests;
    private ProductBacklog emptyBacklogForTests;
    private ProductBacklogInsertion backlogInsertionForTests;
    private Item itemForTests;
    private String itemJsonForTests;
    private String updatedItemJsonForTests;

    private User userForTests;

    private String token;

    private Roadmap roadmapForTests;
    private RoadmapInsertion roadmapInsertionForTests;
    private String itemJsonOnRoadmapForTests;
    private String itemJsonOnRoadmapForTestsUpdated;

    private Sprint sprintForTests;
    private Sprint activeSprint;


    private String sprintJson;
    private String sprintJsonUpdated;

    private SprintInsertion sprintInsertionForTest;

    private String itemJsonForTestsInSprint;


    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProductBacklogService backlogService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ProductBacklogInsertionService insertionService;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private SprintInsertionService sprintInsertionService;

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private RoadmapInsertionService roadmapInsertionService;



    @BeforeEach
    void setupProject() throws Exception {


        if (this.token == null) {
            this.token = this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD);
        }
        if (this.userForTests == null) {
            this.userForTests = userRepository.findByUsername(UniJiraTest.USERNAME).orElse(null);
        }
        while (this.projectForTests == null && userForTests!=null && userForTests.getId()!=null) {
            List<Project> projectList = projectService.findAllByOwnerId(userForTests.getId(),0,10000);
            if (projectList.size() > 0 && projectList.get(0).getId()!=null) {
                this.projectForTests = projectList.get(0);
            }
            else {
                mockMvc.perform(post("/projects")
                                .header("Authorization", "Bearer " + this.token)
                                .contentType("application/json")
                                .content("""
                        {
                            "name": "Test-1",
                            "key": "TS1"
                        }
                        """)
                        )
                        .andExpect(status().isCreated());
            }
        }

        if (this.projectJsonForTests == null && projectForTests!=null && projectForTests.getId()!= null ) {
            Project p = projectService.findById(projectForTests.getId()).orElse(null);
            if (p != null) {
                this.projectJsonForTests =
                        "{ \"project\":{ \"id\" : \""+p.getId()+"\"," +
                                "\"name\": \""+ p.getName()+ "\"," +
                                "\"key\": \""+ p.getKey()+ "\"," +
                                "\"ownerId\": \""+ p.getOwner().getId()+ "\"} }";

            }
        }

        this.setupBacklog();
        this.setupItem();
        this.setupBacklogInsertion();
        this.setupSprint();
        this.setupSprintInsertion();
        this.setupRoadmap();
        this.setupRoadmapInsertion();

    }

    private void setupBacklog()  {

        ProductBacklog backlog = new ProductBacklog();
        backlog.setProject(this.projectForTests);
        this.emptyBacklogForTests = backlogService.save(backlog).orElse(null);

        while (this.backlogForTests == null) {
            if (!this.backlogService.findAllByProject(projectForTests,0,10000).isEmpty()) {
                this.backlogForTests = this.backlogService.findAllByProject(projectForTests, 0, 10000).get(0);
            }
            else {
                backlog = new ProductBacklog();
                backlog.setProject(this.projectForTests);
                this.backlogForTests = backlogService.save(backlog).orElse(null);
            }
        }
    }

    private void setupItem() throws Exception {
        Item i = new Item();
        i.setSummary("DummyItem");
        i.setDescription("this is a Dummy Item and it's so cool");
        i.setType(ItemType.getInstance().EPIC);
        i.setEvaluation(1);

        this.itemForTests = itemService.save(i).orElse(null);

        if (itemForTests!=null && itemForTests.getId()!=null) {

            this.itemJsonForTests = "{\"item\": {" +
                    "\"id\" : \"" + itemForTests.getId() + "\"," +
                    "\"summary\" : \"" + itemForTests.getSummary() + "\"," +
                    "\"description\" : \"" + itemForTests.getDescription() + "\"," +
                    "\"evaluation\" : \"" + itemForTests.getEvaluation() + "\"," +
                    "\"type\" : \"" + itemForTests.getType() + "\"},\"priority\":\"1\" }";

            this.itemJsonForTestsInSprint = "{\"item\": {" +
                    "\"id\" : \"" + itemForTests.getId() + "\"," +
                    "\"summary\" : \"" + itemForTests.getSummary() + "\"," +
                    "\"description\" : \"" + itemForTests.getDescription() + "\"," +
                    "\"evaluation\" : \"" + itemForTests.getEvaluation() + "\"," +
                    "\"type\" : \"" + itemForTests.getType() + "\"}}";

            this.updatedItemJsonForTests = "{\"item\": {" +
                    "\"id\" : \"" + itemForTests.getId() + "\"," +
                    "\"summary\" : \"" + itemForTests.getSummary() + "\"," +
                    "\"description\" : \"" + itemForTests.getDescription() + "\"," +
                    "\"evaluation\" : \"" + itemForTests.getEvaluation() + "\"," +
                    "\"type\" : \"" + itemForTests.getType() + "\"},\"priority\":\"7\" }}";

            this.itemJsonOnRoadmapForTests = "{\"item\": {" +
                    "\"id\" : \"" + itemForTests.getId() + "\"," +
                    "\"summary\" : \"" + itemForTests.getSummary() + "\"," +
                    "\"description\" : \"" + itemForTests.getDescription() + "\"," +
                    "\"evaluation\" : \"" + itemForTests.getEvaluation() + "\"," +
                    "\"type\" : \"" + itemForTests.getType() + "\"},\"startingDate\":\"2022-01-04\",\"endingDate\":\"2022-01-07\"  }";

            this.itemJsonOnRoadmapForTestsUpdated = "{\"item\": {" +
                    "\"id\" : \"" + itemForTests.getId() + "\"," +
                    "\"summary\" : \"" + itemForTests.getSummary() + "\"," +
                    "\"description\" : \"" + itemForTests.getDescription() + "\"," +
                    "\"evaluation\" : \"" + itemForTests.getEvaluation() + "\"," +
                    "\"type\" : \"" + itemForTests.getType() + "\"},\"startingDate\":\"2022-01-04\",\"endingDate\":\"2022-02-07\"  }";
        }
    }

    private void setupBacklogInsertion()  {
        ProductBacklogInsertion insertion = new ProductBacklogInsertion();
        insertion.setBacklog(this.backlogForTests);
        insertion.setItem(this.itemForTests);
        insertion.setPriority(1);
        this.backlogInsertionForTests = insertionService.save(insertion).orElse(null);
    }

    private void setupSprint() {
        Sprint s = new Sprint();
        s.setBacklog(backlogForTests);
        s.setStartingDate(LocalDate.now());
        s.setEndingDate(LocalDate.of(2022,1,31));

        this.sprintForTests = sprintService.save(s).orElse(null);
        if (this.sprintForTests!= null && sprintForTests.getId()!=null) {
            this.sprintJson = "{\n" +
                    "                         \t\"id\": \"" + sprintForTests.getId() + "\",\n" +
                    "                         \t\"startingDate\" : \"2022-01-04\",\n" +
                    "                         \t\"endingDate\" : \"2022-01-31\",\n" +
                    "                         \t\"backlog\" : \"" + this.backlogForTests.getId() + "\"\n" +
                    "                         \t}\n" +
                    "                         }";

            this.sprintJsonUpdated = "{\n" +
                    "                         \t\"id\": \"" + sprintForTests.getId() + "\",\n" +
                    "                         \t\"startingDate\" : \"2022-01-09\",\n" +
                    "                         \t\"endingDate\" : \"2022-02-25\",\n" +
                    "                         \t\"backlog\" : \"" + this.backlogForTests.getId() + "\"\n" +
                    "                         \t}\n" +
                    "                         }";
        }
    }

    private void setupActiveSprint() {
        Sprint s = new Sprint();
        s.setBacklog(backlogForTests);
        s.setStartingDate(LocalDate.now());
        s.setEndingDate(LocalDate.of(2023,1,31));
        s.setStatus(SprintStatus.ACTIVE);

        this.activeSprint = sprintService.save(s).orElse(null);
    }

    private void setupSprintInsertion() {
        SprintInsertion insertion = new SprintInsertion();
        insertion.setSprint(this.sprintForTests);
        insertion.setItem(this.itemForTests);
        this.sprintInsertionForTest = sprintInsertionService.save(insertion).orElse(null);
    }


    private void setupRoadmap() {

        while (this.roadmapForTests == null) {
            if (!this.roadmapService.findByBacklog(backlogForTests,0,10000).isEmpty())
                this.roadmapForTests = this.roadmapService.findByBacklog(backlogForTests,0,10000).get(0);
            else {
                Roadmap roadmap = new Roadmap();
                roadmap.setBacklog(this.backlogForTests);
                this.roadmapForTests = roadmapService.save(roadmap).orElse(null);
            }
        }
    }

    private void setupRoadmapInsertion() {
        RoadmapInsertion insertion = new RoadmapInsertion();
        insertion.setRoadmap(this.roadmapForTests);
        insertion.setStartingDate(LocalDate.of(2022,1,1));
        insertion.setEndingDate(LocalDate.of(2022,12,31));

        this.roadmapInsertionForTests = roadmapInsertionService.save(insertion).orElse(null);
    }


    @Test
    void addBacklogToAProject() throws Exception {
        System.err.println(this.projectJsonForTests);

        mockMvc.perform(post("/projects/"+projectForTests.getId()+"/backlogs")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content(this.projectJsonForTests)
                )
                .andExpect(status().isCreated());

    }

    @Test
    void addBacklogToAProjectImplicit() throws Exception {

        mockMvc.perform(post("/projects/"+projectForTests.getId()+"/backlogs")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {

                        }
                        """)
                )
                .andExpect(status().isCreated());



    }

    @Test
    void readAllBacklogsOfAProject() throws Exception {
        mockMvc.perform(get("/projects/"+projectForTests.getId()+"/backlogs")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                )
                .andExpect(status().isOk());

    }


    @Test
    void readOneBacklog() throws Exception {
        mockMvc.perform(get("/projects/" + projectForTests.getId() + "/backlogs/" + backlogForTests.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                )
                .andExpect(status().isOk());


    }

    @Test
    void deleteOneBacklog() throws Exception {

        mockMvc.perform(delete("/projects/" + projectForTests.getId() + "/backlogs/"+emptyBacklogForTests.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                )
                .andExpect(status().isNoContent());

        Assertions.assertNull(backlogService.findById(emptyBacklogForTests.getId()).orElse(null));
    }

    @Test
    void addItemToOneBacklog() throws Exception {

        mockMvc.perform(post("/projects/" + projectForTests.getId() + "/backlogs/"+backlogForTests.getId()+"/insertions")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(this.itemJsonForTests)
        ).andExpect(status().isCreated());

    }

    @Test
    void readBacklogContent() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/" + projectForTests.getId() + "/backlogs/"+
                backlogForTests.getId()+"/insertions")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();

        call.andExpect(status().isOk());

        System.out.println(returnValue.getResponse().getContentAsString());
    }

    @Test
    void readOneItemFromBacklog() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/" + projectForTests.getId() +
                "/backlogs/"+backlogForTests.getId()+"/insertions/"+backlogInsertionForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();

        call.andExpect(status().isOk());

        System.out.println(returnValue.getResponse().getContentAsString());
    }

    @Test
    void deleteItemFromBacklog() throws Exception {
        ResultActions call = mockMvc.perform(delete("/projects/" + projectForTests.getId()
                + "/backlogs/"+backlogForTests.getId()+"/insertions/"+backlogInsertionForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        call.andExpect(status().isNoContent());

    }

    @Test
    void updateOneFromOneBacklog() throws Exception {

        mockMvc.perform(put("/projects/" + projectForTests.getId()
                + "/backlogs/"+backlogForTests.getId()+"/insertions/"+backlogInsertionForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(updatedItemJsonForTests)
        ).andExpect(status().isOk());
    }

    @Test
    void addSprintToABacklog() throws Exception {

        mockMvc.perform(post("/projects/" + projectForTests.getId()
                + "/backlogs/"+backlogForTests.getId()+"/sprints")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(this.sprintJson)
        ).andExpect(status().isCreated());
    }

    @Test
    void sprintsOfABacklogTest() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/" + projectForTests.getId()
                + "/backlogs/"+backlogForTests.getId()+"/sprints")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));
        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());


    }

    @Test
    void sprintById() throws Exception {

        System.out.println("Calling: "+"/projects/" + projectForTests.getId()
                + "/backlogs/"+backlogForTests.getId()+"/sprints/"+sprintForTests.getId());
        ResultActions call = mockMvc.perform(get("/projects/" + projectForTests.getId()
                + "/backlogs/"+backlogForTests.getId()+"/sprints/"+sprintForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));

        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());


    }


    @Test
    void updateSprint() throws Exception {

        mockMvc.perform(put("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/sprints/"+sprintForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(sprintJsonUpdated)
        ).andExpect(status().isOk());
    }

    @Test
    void deleteSprint() throws Exception {

        if (!sprintInsertionService.findItemsBySprint(sprintForTests,0,10000).isEmpty()) {
            List <SprintInsertion> toDelete = sprintInsertionService.findItemsBySprint(sprintForTests,0,10000);
            for (SprintInsertion toDeleteInsertion : toDelete) {
                sprintInsertionService.delete(toDeleteInsertion);
            }

        }

        mockMvc.perform(delete("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/sprints/"+sprintForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isNoContent());

    }


    @Test
    void addItemToSprint() throws Exception {


        mockMvc.perform(post("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/sprints/"+sprintForTests.getId()+"/insertions")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(this.itemJsonForTestsInSprint)
        ).andExpect(status().isCreated());


    }

    @Test
    void readItemsFromSprint() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/sprints/"+sprintForTests.getId()+"/insertions")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));

        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());

    }

    @Test
    void readOneItemFromOneSprint() throws Exception {


        ResultActions call = mockMvc.perform(get("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/sprints/"+sprintForTests.getId()+"/insertions/"+sprintInsertionForTest.getId())
                .header("Authorization",
                        "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());


    }

    @Test
    void deleteItemOfASprint() throws Exception {



        ResultActions call = mockMvc.perform(delete("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/sprints/"+sprintForTests.getId()+"/insertions/"+sprintInsertionForTest.getId())
                .header("Authorization",
                        "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));

        call.andExpect(status().isNoContent());

        System.out.println(sprintInsertionService.findAll().size());
    }


    @Test
    void addRoadmap() throws Exception {

        mockMvc.perform(post("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/roadmaps")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content("""
                        {

                         	}
                         }
                                    """)
        ).andExpect(status().isCreated());

    }

    @Test
    void readRoadmapsFromBacklog() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/" + projectForTests.getId()
                + "/backlogs/"+backlogForTests.getId()+"/roadmaps")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));

        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());

    }

    @Test
    void readOneRoadmapFromBacklog() throws Exception {




        ResultActions call = mockMvc.perform(get("/projects/" + projectForTests.getId()
                + "/backlogs/"+backlogForTests.getId()+"/roadmaps/"+roadmapForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());

    }


    @Test
    void deleteRoadmap() throws Exception {

        List<RoadmapInsertion> insertionList = roadmapInsertionService.findAllByRoadmap(roadmapForTests,0,20000);
        for (RoadmapInsertion insertion : insertionList) {
            roadmapInsertionService.delete(insertion);
        }

        ResultActions call = mockMvc.perform(delete("/projects/" + projectForTests.getId()
                + "/backlogs/"+backlogForTests.getId()+"/roadmaps/"+roadmapForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));

        call.andExpect(status().isNoContent());


    }

    @Test
    void addItemToRoadmap() throws Exception {

        mockMvc.perform(post("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/roadmaps/"+roadmapForTests.getId()+"/insertions")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(itemJsonOnRoadmapForTests)
        ).andExpect(status().isCreated());

    }

    @Test
    void readItemsFromRoadmap() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/roadmaps/"+roadmapForTests.getId()+"/insertions")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());
    }

    @Test
    void readOneItemFromRoadmap() throws Exception {


        ResultActions call = mockMvc.perform(get("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/roadmaps/"+roadmapForTests.getId()+"/insertions/"+roadmapInsertionForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());

    }

    @Test
    void updateItemsOfARoadmap() throws Exception {

        mockMvc.perform(put("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/roadmaps/"+roadmapForTests.getId()+"/insertions/"+roadmapInsertionForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(itemJsonOnRoadmapForTestsUpdated)
        ).andExpect(status().isOk());

    }


    @Test
    void deleteItemOfARoadmap() throws Exception {
        ResultActions call = mockMvc.perform(delete("/projects/" + projectForTests.getId() + "/backlogs/"
                +backlogForTests.getId()+"/roadmaps/"+roadmapForTests.getId()+"/insertions/"+roadmapInsertionForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isNoContent());

    }


    @Test
    void activeSprint() throws Exception {
        this.setupActiveSprint();

        ResultActions call = mockMvc.perform(get("/projects/" + projectForTests.getId()
                + "/sprint")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));

        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());

    }



}
