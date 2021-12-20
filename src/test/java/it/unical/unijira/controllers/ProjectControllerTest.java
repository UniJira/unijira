package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ProjectControllerTest extends UniJiraTest {

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
    void sendInvitationsSuccessful() throws Exception {

        mockMvc.perform(post("/projects/invitations")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content("""
                        {
                            "projectId": "4",
                            "emails" : ["%s"]
                        }
                        """.formatted(UniJiraTest.USERNAME))
        )
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("[]"))))
        .andDo(print());

    }

    @Test
    void acceptInvitationsSuccessful() throws Exception {

        mockMvc.perform(get("/projects/accept")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .param(
                            "token",
                                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLXNlcnZpY2UtdW5pamlyYSIsImV4cCI6MTYzOTk1NjM0NywidHlwZSI6IlBST0pFQ1RfSU5WSVRFIiwiaWF0IjoxNjM5OTUyNzQ3LCJwcm9qZWN0SWQiOiIyIiwidXNlcklkIjoiMSJ9.GvVDtbG5shn8AtXze3Yl0zt7fi7Rn3A3437pNy_FQmk"
                        )
                )
                .andExpect(status().isOk())
                .andDo(print());

    }

}
