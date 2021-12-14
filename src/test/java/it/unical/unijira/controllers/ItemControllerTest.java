package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.ProductBacklogItem;
import it.unical.unijira.utils.ProductBacklogItemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemControllerTest extends UniJiraTest {

    @BeforeEach
    void insertDummyObject(){
        ProductBacklogItem father = new ProductBacklogItem();
        father.setDescription("this is an useless epic");
        father.setEvaluation(77);
        father.setMeasureUnit("metri");
        father.setSummary("useless epic");
        try {
            father.setType(ProductBacklogItemType.getInstance().EPIC);
            father.setFather(null);
        } catch (NonValidItemTypeException e) {}

        father.setOwner(userRepository.findAll().stream().findFirst().get());
        father.setTags("#backend#");


        father = pbiRepository.saveAndFlush(father);
    }


    @Test
    void retrieveAllItems() throws Exception {
        mockMvc.perform(get("/backlog/items").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":")));
    }

    @Test
    void newItem() throws Exception {
        mockMvc.perform(post("/backlog/items")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content("""
                        {
                            "summary": "HELLO I'M AN EPIC",
                            "description": "HI I'M AN EPIC AND I WANTO TO BE A GOOD EPIC",
                            "measureUnit": "story points",
                            "evaluation": "100000",
                            "tags" : "#BACKEND#;#FRONTEND#",
                            "type" : "epic",
                            "owner" : { "id": 1, "username" : "unijira20@gmail.com"}
                        }
                        """)
        ).andExpect(status().isCreated());
    }

    @Test
    void retrieveById() throws Exception {
        mockMvc.perform(get("/backlog/items/1")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":")));
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/backlog/items/1")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateProjectSuccessful() throws Exception {

        mockMvc.perform(put("/backlog/items/1")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "summary": "HELLO I'M A STORY",
                            "description": "HI I'M AN EPIC AND I WANTO TO BE A GOOD STORY",
                            "measureUnit": "story points",
                            "evaluation": "100033",
                            "tags" : "#BACKEND#;#FRONTEND#",
                            "type" : "story",
                            "owner" : { "id": 1, "username" : "unijira20@gmail.com"}
                        }
                        """)
                )
                .andExpect(status().isOk());

    }

}
