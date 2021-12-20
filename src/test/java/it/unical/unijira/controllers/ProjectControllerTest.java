package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.models.*;
import it.unical.unijira.services.common.*;
import it.unical.unijira.utils.ItemType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ProjectControllerTest extends UniJiraTest {

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

    @Test
    void readAllProjectSuccessful() throws Exception {

        mockMvc.perform(get("/projects").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("[]"))));

    }

    @Test
    void readSingleProjectSuccessful() throws Exception {

        mockMvc.perform(get("/projects/3").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk());

    }

    @Test
    void createProjectSuccessful() throws Exception {

        mockMvc.perform(post("/projects")
               .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
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

    @Test
    void readAllRolesSuccessful() throws Exception {

        mockMvc.perform(get("/projects/3/memberships").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("[]"))));

    }

    @Test
    void updateProjectSuccessful() throws Exception {

        mockMvc.perform(put("/projects/3")
               .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
               .contentType("application/json")
               .content("""
                        {
                            "id": "3",
                            "name": "Test-Updated",
                            "key": "TSU",
                            "ownerId": "1"
                        }
                        """)
                )
               .andExpect(status().isOk());

    }

    @Test
    void deleteProjectSuccessful() throws Exception {

        mockMvc.perform(delete("/projects/3")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                        .andExpect(status().isNoContent());

    }

    @Test
    void addBacklogToAProject() throws Exception {
        mockMvc.perform(post("/projects")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "name": "Test-1",
                            "key": "TS1"
                        }
                        """)
                )
                .andExpect(status().isCreated());

        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();

        mockMvc.perform(post("/projects/"+firstId+"/backlogs")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "project": {
                            "id" : "3",
                            "name": "Test",
                            "key": "TST",
                            "ownerId" : "1"
                            }
                        }
                        """)
                )
                .andExpect(status().isCreated());

    }

    @Test
    void addBacklogToAProjectImplicit() throws Exception {
        mockMvc.perform(post("/projects")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "name": "Test-1",
                            "key": "TS1"
                        }
                        """)
                )
                .andExpect(status().isCreated());

        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();

        mockMvc.perform(post("/projects/"+firstId+"/backlogs")
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
        mockMvc.perform(post("/projects")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "name": "Test-1",
                            "key": "TS1"
                        }
                        """)
                )
                .andExpect(status().isCreated());

        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();



        mockMvc.perform(get("/projects/"+firstId+"/backlogs")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                )
                .andExpect(status().isOk());

    }

    @Test
    void readOneBacklog() throws Exception {

        mockMvc.perform(post("/projects")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "name": "Test-1",
                            "key": "TS1"
                        }
                        """)
                )
                .andExpect(status().isCreated());

        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();

        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(projectList.get(0));
        if (backlogsOfThisProject.size() ==  0) {

            mockMvc.perform(post("/projects/" + firstId + "/backlogs")
                            .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                            .contentType("application/json")
                            .content("""
                                    {
                                        
                                    }
                                    """)
                    )
                    .andExpect(status().isCreated());

            backlogsOfThisProject = backlogService.findAllByProject(projectList.get(0));

        }


        mockMvc.perform(get("/projects/" + firstId + "/backlogs/" + backlogsOfThisProject.get(0).getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                )
                .andExpect(status().isOk());


    }


    @Test
    void deleteOneBacklog() throws Exception {
        this.addBacklogToAProjectImplicit();
        this.readOneBacklog();
        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();
        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(projectList.get(0));

        mockMvc.perform(delete("/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                )
                .andExpect(status().isNoContent());

        Assertions.assertNull(backlogService.findById(backlogsOfThisProject.get(0).getId()).orElse(null));
    }

    @Test
    void addItemToOneBacklog() throws Exception {

        Item i = new Item();
        i.setId(1L);
        i.setSummary("schifo di item");
        i.setDescription("questo schifo di item");
        i.setType(ItemType.getInstance().EPIC);
        i.setEvaluation(1);

        itemService.save(i);

        this.addBacklogToAProjectImplicit();
        this.readOneBacklog();
        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();
        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(projectList.get(0));

        System.out.println("Calling: "+"/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/items");

        mockMvc.perform(post("/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/items")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content("""
                                    {
                                        "item": {
                                            "id" : "1",
                                            "summary" : "schifo di item",
                                            "description" : "questo schifo di item ",
                                            "evaluation" : "1",
                                            "type" : "epic"

                                        },
                                        "priority": "1"
                                    }
                                    """)
        ).andExpect(status().isCreated());


        List<ProductBacklogInsertion> tmp =this.insertionService.findAllByBacklog(backlogsOfThisProject.get(0));
        System.out.println(tmp.size());
    }

    @Test
    void readBacklogContent() throws Exception {

        this.addItemToOneBacklog();

        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();
        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(projectList.get(0));



        System.out.println("Calling: "+"/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/items");

        ResultActions call = mockMvc.perform(get("/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/items")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();

        call.andExpect(status().isOk());

        System.out.println(returnValue.getResponse().getContentAsString());
    }

    @Test
    void readOneBacklogContent() throws Exception {
        this.addItemToOneBacklog();
        Project pj = projectService.findById(3L).orElse(null);
        long firstId = pj.getId();
        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(pj);



        System.out.println("Calling: "+"/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/items");

        ResultActions call = mockMvc.perform(get("/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/items/1")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();

        call.andExpect(status().isOk());

        System.out.println(returnValue.getResponse().getContentAsString());
    }

    @Test
    void deleteItemFromBacklog() throws Exception {
        this.addItemToOneBacklog();
        Project pj = projectService.findById(3L).orElse(null);
        long firstId = pj.getId();
        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(pj);



        System.out.println("Calling: "+"/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/items");

        ResultActions call = mockMvc.perform(delete("/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/items/1")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();

        call.andExpect(status().isNoContent());

    }

    @Test
    void updateOneFromOneBacklog() throws Exception {
        this.addItemToOneBacklog();
        Project pj = projectService.findById(3L).orElse(null);
        long firstId = pj.getId();
        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(pj);



        System.out.println("Calling: "+"/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/items");

        mockMvc.perform(put("/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/items/1")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
         .contentType("application/json")
                .content("""
                                    {
                                        "item": {
                                            "id" : "1",
                                            "summary" : "schifo di item",
                                            "description" : "questo schifo di item ",
                                            "evaluation" : "1",
                                            "type" : "epic"

                                        },
                                        "priority": "3"
                                    }
                                    """)
        ).andExpect(status().isOk());
    }


    @Test
    void addSprintToABacklog() throws Exception{
        this.addBacklogToAProjectImplicit();

        this.readOneBacklog();
        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();
        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(projectList.get(0));

        System.out.println("Calling: "+"/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/sprints");

        String x =this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD);
        System.out.println(x);

        mockMvc.perform(post("/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/sprints")
                .header("Authorization", "Bearer " + x)
                .contentType("application/json")
                .content("""
                        {
                         	"id": "1",
                         	"startingDate" : "2020-12-20",
                         	"endingDate" : "2020-12-31",
                         	"backlog" : "1"
                         	}
                         }
                                    """)
        ).andExpect(status().isCreated());

    }

    @Test
    void sprintsOfABacklogTest() throws Exception {
        this.addSprintToABacklog();



        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();
        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(projectList.get(0));

        System.out.println("Calling: "+"/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/sprints");
        ResultActions call = mockMvc.perform(get("/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/sprints")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());


    }

    @Test
    void sprintById() throws Exception {
        this.addSprintToABacklog();

        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();
        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(projectList.get(0));

        List<Sprint> sprints=  sprintService.findSprintsByBacklog(backlogsOfThisProject.get(0));

        System.out.println("Calling: "+"/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/sprints/"+sprints.get(0).getId());
        ResultActions call = mockMvc.perform(get("/projects/" + firstId + "/backlogs/"+backlogsOfThisProject.get(0).getId()+"/sprints/"+sprints.get(0).getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());


    }


    @Test
    void updateSprint() throws Exception {
        this.addSprintToABacklog();



        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();
        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(projectList.get(0));

        List<Sprint> sprints=  sprintService.findSprintsByBacklog(backlogsOfThisProject.get(0));

        System.out.println("Calling: "+"/projects/" + firstId + "/backlogs/"
                +backlogsOfThisProject.get(0).getId()+"/sprints/"+sprints.get(0).getId());

        String x =this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD);
        System.out.println(x);

        mockMvc.perform(put("/projects/" + firstId + "/backlogs/"
                +backlogsOfThisProject.get(0).getId()+"/sprints/"+sprints.get(0).getId())
                .header("Authorization", "Bearer " + x)
                .contentType("application/json")
                .content("""
                        {
                         	"id": "1",
                         	"startingDate" : "2020-12-30",
                         	"endingDate" : "2020-12-31",
                         	"backlog" : "1"
                         	}
                         }
                                    """)
        ).andExpect(status().isOk());
    }

    @Test
    void deleteSprint() throws Exception {
        this.addSprintToABacklog();
        List<Project> projectList = projectService.findAllByOwnerId(1L,0,10000);
        long firstId = projectList.get(0).getId();
        List<ProductBacklog> backlogsOfThisProject = backlogService.findAllByProject(projectList.get(0));

        List<Sprint> sprints=  sprintService.findSprintsByBacklog(backlogsOfThisProject.get(0));

        System.out.println("Calling: "+"/projects/" + firstId + "/backlogs/"
                +backlogsOfThisProject.get(0).getId()+"/sprints/"+sprints.get(0).getId());

        String x =this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD);
        System.out.println(x);

        mockMvc.perform(delete("/projects/" + firstId + "/backlogs/"
                        +backlogsOfThisProject.get(0).getId()+"/sprints/"+sprints.get(0).getId())
                        .header("Authorization", "Bearer " + x)).andExpect(status().isNoContent());

    }

    @Test
    void addItemToSprint() throws Exception {

    }

    @Test
    void readItemsFromSprint() throws Exception {

    }

    @Test
    void readOneItemFromOneSprint() throws Exception {

    }

    @Test
    void updateItemsOfASprint() throws Exception {

    }

    @Test
    void deleteItemOfASprint() throws Exception {

    }

}
