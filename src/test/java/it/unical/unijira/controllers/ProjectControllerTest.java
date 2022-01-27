package it.unical.unijira.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.dto.projects.DefinitionOfDoneEntryDTO;
import it.unical.unijira.data.models.projects.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
    void readMembershipsPermissionSuccessful() throws Exception {

        var projectId = projectRepository.findAll()
                .stream()
                .filter(i ->  i.getOwner().getId().equals(1L))
                .findAny()
                .stream()
                .mapToLong(Project::getId)
                .findAny()
                .orElseThrow(() -> new RuntimeException("Project owned by User id(1) not found"));

        mockMvc.perform(get("/projects/" + projectId + "/memberships/1/permission/DETAILS")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk())
                .andDo(print());

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


    String createDefOfDoneEntry() throws Exception {
        return mockMvc.perform(post("/projects/" + this.dummyProject.getId() + "/defofdone")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "description": "Ciaone"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void updateDefOfDoneEntry() throws Exception {
        DefinitionOfDoneEntryDTO created = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(createDefOfDoneEntry(), DefinitionOfDoneEntryDTO.class);

        mockMvc.perform(put("/projects/" + this.dummyProject.getId() + "/defofdone/" + created.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "id": "%d",
                            "description": "Amico mio",
                            "priority": "%d",
                            "projectId": "%d"
                        }
                        """.formatted(
                                created.getId(), created.getPriority(), created.getProjectId()
                        ))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void updateDefOfDoneEntryFailure() throws Exception {
        DefinitionOfDoneEntryDTO created = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(createDefOfDoneEntry(), DefinitionOfDoneEntryDTO.class);

        mockMvc.perform(put("/projects/" + this.dummyProject.getId() + "/defofdone/" + created.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "id": "%d",
                            "description": "Amico mio",
                            "priority": "%d",
                            "projectId": "%d"
                        }
                        """.formatted(
                                created.getId(), 0, created.getProjectId() + 1
                        ))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void updateDefOfDoneEntrySwitchPriorities() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        DefinitionOfDoneEntryDTO created1 = mapper.readValue(createDefOfDoneEntry(), DefinitionOfDoneEntryDTO.class);
        DefinitionOfDoneEntryDTO created2 = mapper.readValue(createDefOfDoneEntry(), DefinitionOfDoneEntryDTO.class);

        Assertions.assertEquals(1, created1.getPriority());
        Assertions.assertEquals(2, created2.getPriority());


        String updateResponse = mockMvc.perform(put("/projects/" + this.dummyProject.getId() + "/defofdone/" + created1.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "id": "%d",
                            "description": "Amico mio",
                            "priority": "%d",
                            "projectId": "%d"
                        }
                        """.formatted(
                                created1.getId(), created2.getPriority(), created1.getProjectId()
                        ))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        String secondEntryGetResponse = mockMvc.perform(get("/projects/" + this.dummyProject.getId() + "/defofdone/" + created2.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(2, mapper.readValue(updateResponse, DefinitionOfDoneEntryDTO.class).getPriority());
        Assertions.assertEquals(1, mapper.readValue(secondEntryGetResponse, DefinitionOfDoneEntryDTO.class).getPriority());

    }

    @Test
    void deleteDefOfDoneEntry() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        DefinitionOfDoneEntryDTO created1 = mapper.readValue(createDefOfDoneEntry(), DefinitionOfDoneEntryDTO.class);
        DefinitionOfDoneEntryDTO created2 = mapper.readValue(createDefOfDoneEntry(), DefinitionOfDoneEntryDTO.class);

        Assertions.assertEquals(1, created1.getPriority());
        Assertions.assertEquals(2, created2.getPriority());


        mockMvc.perform(delete("/projects/" + this.dummyProject.getId() + "/defofdone/" + created1.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))

                )
                .andExpect(status().isNoContent())
                .andDo(print());

        mockMvc.perform(get("/projects/" + this.dummyProject.getId() + "/defofdone/" + created1.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                )
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        String secondEntryGetResponse = mockMvc.perform(get("/projects/" + this.dummyProject.getId() + "/defofdone/" + created2.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();


        Assertions.assertEquals(1, mapper.readValue(secondEntryGetResponse, DefinitionOfDoneEntryDTO.class).getPriority());

    }

    @Test
    void readAllDefOfDoneEntriesByProject() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        DefinitionOfDoneEntryDTO created1 = mapper.readValue(createDefOfDoneEntry(), DefinitionOfDoneEntryDTO.class);
        mapper.readValue(createDefOfDoneEntry(), DefinitionOfDoneEntryDTO.class);


        String result = mockMvc.perform(get("/projects/" + this.dummyProject.getId() + "/defofdone/")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))

                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<DefinitionOfDoneEntryDTO> resultList = mapper.readValue(result, new TypeReference<>() {});

        Assertions.assertFalse(resultList.isEmpty());
        Assertions.assertEquals(2, resultList.size());

        Assertions.assertEquals(
                created1.getId(),
                resultList.stream()
                        .filter(r -> r.getPriority().equals(1))
                        .findFirst()
                        .map(DefinitionOfDoneEntryDTO::getId)
                        .orElseThrow()
        );

        mockMvc.perform(delete("/projects/" + this.dummyProject.getId() + "/defofdone/" + created1.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))

                )
                .andExpect(status().isNoContent())
                .andDo(print());

        result = mockMvc.perform(get("/projects/" + this.dummyProject.getId() + "/defofdone/")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))

                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        resultList = mapper.readValue(result, new TypeReference<>() {});
        Assertions.assertEquals(1, resultList.size());

    }

}