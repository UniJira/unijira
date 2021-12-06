package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class NotifyControllerTest extends UniJiraTest {


    @Test
    void readAllNotifiesSuccessful() throws Exception {

        mockMvc.perform(get("/notifies").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("[]"))));

    }

    @Test
    void readSingleNotificationSuccessful() throws Exception {

        mockMvc.perform(get("/notifies/2").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk());

    }

    @Test
    void readSingleNotificationUnsuccessful() throws Exception {

        mockMvc.perform(get("/notifies/9999").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isNotFound());

    }

}
