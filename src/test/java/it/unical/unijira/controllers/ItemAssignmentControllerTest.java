package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.items.*;
import it.unical.unijira.services.common.ItemAssignmentService;
import it.unical.unijira.services.common.ItemService;
import it.unical.unijira.services.common.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ItemAssignmentControllerTest extends UniJiraTest {

    private Item itemForTests;
    private ItemAssignment assignmentForTests;

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemAssignmentService itemAssignmentService;
    @Autowired
    private UserService userService;
    private User newUser;

    private String assignmentJson;
    private String assignmentJsonUpdated;


    @BeforeEach
    void initAllStuff() {

        User userForTests = userService.findByUsername(UniJiraTest.USERNAME).orElse(null);

        if (this.newUser == null) {
            User anotherUser = User.builder()
                    .username(System.currentTimeMillis()+"updated@gmail.com")
                    .password("SxI0890!klWdssdW90_09823hjkshndfakJKH")
                    .status(User.Status.ACTIVE)
                    .build();

            this.newUser = userService.save(anotherUser).orElse(null);
        }

        Item forTests = Item.builder()
                .summary("As a test item i want to test myself so that i can be tested")
                .description("This item is a test item which has to be tested. Is it clear?")
                .measureUnit(MeasureUnit.STORY_POINTS)
                .type(ItemType.EPIC)
                .evaluation(7)
                .status(ItemStatus.OPEN)
                .build();

        this.itemForTests = itemService.save(forTests).orElse(null);

        ItemAssignment assignmentForTests = ItemAssignment.builder()
                .item(this.itemForTests)
                .assignee(userForTests)
                .build();

        this.assignmentForTests = itemAssignmentService.save(assignmentForTests).orElse(null);


        assert this.assignmentForTests != null;
        this.assignmentJson = "{ \"itemId\" : \"" + this.assignmentForTests.getItem().getId() + "\","
                    + "\"assigneeId\" : \"" + this.newUser.getId() + "\"}";
        System.out.println(this.assignmentJson);

        this.assignmentJsonUpdated = "{ \"id\" : \""+this.assignmentForTests.getId()+"\","
                +"\"itemId\" : \""+this.assignmentForTests.getItem().getId()+"\","
                +"\"assigneeId\" : \""+this.newUser.getId()+"\"}";

        System.out.println(this.assignmentJsonUpdated);
    }


    @Test
    void createAssignment() throws Exception {

        int initialSize = itemAssignmentService.findAllByItem(this.itemForTests,0,10000).size();
        mockMvc.perform(post("/items/"+itemForTests.getId()+"/assignments")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content(this.assignmentJson))
                .andExpect(status().isCreated());

        int finalSize =   itemAssignmentService.findAllByItem(this.itemForTests,0,10000).size();

        Assertions.assertEquals(initialSize + 1, finalSize);

    }

    @Test
    void readAssignmentsForItem() throws Exception {
        ResultActions call = mockMvc.perform(get("/items/"+itemForTests.getId()+"/assignments")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());
    }

    @Test
    void readAssignmentByIde() throws Exception {
        ResultActions call = mockMvc.perform(get("/items/"+itemForTests.getId()+"/assignments/"+assignmentForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());
    }

    @Test
    void updateAssignment() throws Exception {
        ResultActions call = mockMvc.perform(put("/items/"+itemForTests.getId()+"/assignments/"+assignmentForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(assignmentJsonUpdated)
        );
        String result = call.andReturn().getResponse().getContentAsString();
        System.out.println(result);
        call.andExpect(status().isOk());
        Assertions.assertTrue(result.contains("updated@gmail.com"));
    }

    @Test
    void deleteAssignment() throws Exception {
        int initialSize = itemAssignmentService.findAllByItem(this.itemForTests,0,10000).size();

        mockMvc.perform(delete("/items/"+itemForTests.getId()+"/assignments/"+assignmentForTests.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isNoContent());

        int finalSize = itemAssignmentService.findAllByItem(this.itemForTests,0,10000).size();

        Assertions.assertTrue(initialSize>finalSize);
    }
}
