package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.models.Notify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class NotifyControllerTest extends UniJiraTest {


    @Test
    void readAllNotificationsSuccessful() throws Exception {

        mockMvc.perform(get("/notifications").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("[]"))));

    }

    @Test
    void readSingleNotificationSuccessful() throws Exception {

        mockMvc.perform(get("/notifications/2").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk());

    }

    @Test
    void readSingleNotificationUnsuccessful() throws Exception {

        mockMvc.perform(get("/notifications/9999").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isNotFound());

    }

    @Test
    void markAllAsReadAndReadSuccessful() throws Exception {

        mockMvc.perform(put("/notifications/mark").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk());

        Assertions.assertTrue(notifyRepository
                .findByUserIdOrderByReadAscPriorityAscCreatedAtDesc(1L, PageRequest.of(0, 25))
                .stream()
                .allMatch(Notify::isRead));

    }

}
