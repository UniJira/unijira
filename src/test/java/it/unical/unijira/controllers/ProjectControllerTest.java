package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.models.projects.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ProjectControllerTest extends UniJiraTest {

    private Project dummyProject;
    @BeforeEach
    void initProject() {
        Project p = new Project();
        p.setName("DUMMY PROJECT");
        p.setKey("KEY");
        p.setOwner(userRepository.findByUsername(UniJiraTest.USERNAME).orElse(null));
        this.dummyProject = projectRepository.saveAndFlush(p);

    }

    @Test
    void readAllProjectSuccessful() throws Exception {

        mockMvc.perform(get("/projects").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(equalTo("[]"))));

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
        mockMvc.perform(delete("/projects/"+this.dummyProject.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                        .andExpect(status().isNoContent());

    }


    @Test
    void sendInvitationsSuccessful() throws Exception {

        var projectId = projectRepository.findAll()
                        .stream()
                        .filter(i ->  i.getOwner().getId().equals(1L))
                        .findAny()
                        .stream()
                        .mapToLong(Project::getId)
                        .findAny()
                        .orElseThrow(() -> new RuntimeException("Project owned by User id(1) not found"));

        mockMvc.perform(post("/projects/invitations")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content("""
                        {
                            "projectId": "%d",
                            "emails" : ["%s"]
                        }
                        """.formatted(projectId, "test@user.com"))
        )
        .andExpect(status().isOk())
        .andDo(print());

    }

    @Test
    void readMembershipsSuccessful() throws Exception {

        mockMvc.perform(get("/projects/3/memberships").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("[]"))))
                .andDo(print());

    }

    @Test
    void updateMembership() throws Exception {

        var projectId = projectRepository.findAll()
                .stream()
                .filter(i ->  i.getOwner().getId().equals(1L))
                .findAny()
                .stream()
                .mapToLong(Project::getId)
                .findAny()
                .orElseThrow(() -> new RuntimeException("Project owned by User id(1) not found"));

        mockMvc.perform(put("/projects/" + projectId + "/memberships/1")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "keyUserId": "%d",
                            "keyProjectId": "%d",
                            "role": "MEMBER",
                            "status": "PENDING",
                            "permissions": ["%s"]
                        }
                        """.formatted(1, projectId, "DETAILS"))
                )
                .andExpect(status().isOk())
                .andDo(print());

    }


}
