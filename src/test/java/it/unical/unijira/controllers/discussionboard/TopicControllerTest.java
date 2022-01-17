package it.unical.unijira.controllers.discussionboard;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.discussions.Message;
import it.unical.unijira.data.models.discussions.Topic;
import it.unical.unijira.data.models.discussions.TopicType;
import it.unical.unijira.data.models.projects.Membership;
import it.unical.unijira.data.models.projects.MembershipKey;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.common.UserService;
import it.unical.unijira.services.discussionboard.MessageService;
import it.unical.unijira.services.discussionboard.TopicService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TopicControllerTest extends UniJiraTest {

    private Project projectForTests;
    private User userForTests;
    private Topic topicForTests;
    private Topic toDeleteForTests;
    private Message messageForTests;


    @Autowired
    private MessageService messageService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    private String topicJsonForTests;
    private String topicJsonForTestsUpdated;
    private String messageJsonForTests;
    private String messageJSonForTestsUpdated;

    public TopicControllerTest() {

    }

    @BeforeEach
    void setupAllStuff() {

        this.userForTests = userService.findByUsername(UniJiraTest.USERNAME).orElse(null);

        Project project = Project.builder()
                .owner(this.userForTests)
                .name("Test")
                .key("TST")
                .memberships(Collections.emptyList())
                .build();

        this.projectForTests = projectRepository.saveAndFlush(project);


        Membership membership = Membership.builder()
                .status(Membership.Status.ENABLED)
                .role(Membership.Role.SCRUM_MASTER)
                .key(new MembershipKey(userForTests, project))
                .permissions(new HashSet<>(Arrays.asList(Membership.Permission.ADMIN, Membership.Permission.DETAILS,
                        Membership.Permission.INVITATIONS, Membership.Permission.ROLES)))
                .build();

        userProjectRepository.saveAndFlush(membership);


        this.setupTopic();

    }



    private void setupTopic() {

        this.topicJsonForTests =  "{\n" +
                "                         \t\"title\" : \"SUMMARY\",\n" +
                "                         \t\"content\" : \"CONTENT\",\n" +
                "                         \t\"type\" : \"GENERAL\",\n" +
                "                         \t\"projectId\" : \""+projectForTests.getId()+"\",\n" +
                "                         \t\"authorId\" : \"" + this.userForTests.getId() + "\"\n" +
                "                         \t}\n" +
                "                         }";

        Topic forTests = Topic.builder()
                .author(userForTests)
                .project(projectForTests)
                .title("This is a wonderful topic to discuss about")
                .content("asdasdasd")
                .type(TopicType.GENERAL).build();
        this.topicForTests = this.topicService.save(forTests).orElse(null);

        Topic toDelete = Topic.builder()
                .author(userForTests)
                .project(projectForTests)
                .title("This is a bad topic and need to be deleted")
                .content("qweqweqwer")
                .type(TopicType.GENERAL).build();

        this.toDeleteForTests = this.topicService.save(toDelete).orElse(null);

        this.topicJsonForTestsUpdated =  "{\"id\" : \""+this.topicForTests.getId()+"\",\n" +
                "                         \t\"title\" : \"SUMMARY UPDATED\",\n" +
                "                         \t\"type\" : \"IDEAS\",\n" +
                "                         \t\"content\" : \"CONTENT UPDATED\",\n" +
                "                         \t\"projectId\" : \""+projectForTests.getId()+"\",\n" +
                "                         \t\"authorId\" : \"" + this.userForTests.getId() + "\"\n" +
                "                         \t}\n" +
                "                         }";

        Message message = Message.builder()
                .author(userForTests)
                .topic(topicForTests)
                .content("THIS IS THE FIRST MESSAGE").build();

        this.messageForTests = this.messageService.save(message).orElse(null);

        Message reply = Message.builder()
                .author(userForTests)
                .topic(topicForTests)
                .content("THIS IS MY REPLY TO YOUR PREVIOUS MESSAGE")
                .repliesTo(this.messageForTests).build();

        this.messageService.save(reply);


        this.messageJsonForTests =  "{\t\"content\" : \""+this.messageForTests.getContent()+"\",\n" +
                "                         \t\"topicId\" : \""+this.messageForTests.getTopic().getId()+"\",\n" +
                "                         \t\"authorUsername\" : \""+this.messageForTests.getAuthor().getUsername()+"\",\n" +
                "                         \t\"authorId\" : \"" + this.messageForTests.getAuthor().getId() + "\"\n" +
                "                         \t}\n" +
                "                         }";

        this.messageJSonForTestsUpdated = "{\"id\" : \""+this.messageForTests.getId()+"\",\n" +
                "                         \t\"content\" : \""+this.messageForTests.getContent()+" UPDATED"+"\",\n" +
                "                         \t\"topicId\" : \""+this.messageForTests.getTopic().getId()+"\",\n" +
                "                         \t\"authorUsername\" : \""+this.messageForTests.getAuthor().getUsername()+"\",\n" +
                "                         \t\"authorId\" : \"" + this.messageForTests.getAuthor().getId() + "\"\n" +
                "                         \t}\n" +
                "                         }";






    }

    @Test
    void createTopic() throws Exception{

        int initialSize = topicService.findAll(projectForTests.getId(),0,100000).size();
        mockMvc.perform(post("/projects/"+projectForTests.getId()+"/topics")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(this.topicJsonForTests))
                .andExpect(status().isCreated());

        int finalSize =  topicService.findAll(projectForTests.getId(),0,100000).size();

        Assertions.assertEquals(initialSize + 1, finalSize);

    }

    @Test
    void retrieveAllTopics() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/"+projectForTests.getId()+"/topics")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());

    }


    @Test
    void retrieveOne() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/"+projectForTests.getId()+"/topics/"+topicForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());

    }

    @Test
    void updateOne() throws Exception {

        ResultActions call = mockMvc.perform(put("/projects/"+projectForTests.getId()+"/topics/"+topicForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(topicJsonForTestsUpdated)
        );
        String result = call.andReturn().getResponse().getContentAsString();
        System.out.println(result);
        call.andExpect(status().isOk());
        Assertions.assertTrue(result.contains("UPDATED"));
    }

    @Test
    void deleteOne() throws Exception {

        int initialSize = topicService.findAll(projectForTests.getId(),0,100000).size();

        mockMvc.perform(delete("/projects/"+projectForTests.getId()+"/topics/"+toDeleteForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isNoContent());

        int finalSize =  topicService.findAll(projectForTests.getId(),0,100000).size();

        Assertions.assertTrue(initialSize>finalSize);

    }

    @Test
    void deleteOneNotEmpty() throws Exception {

        int initialSize = topicService.findAll(projectForTests.getId(),0,100000).size();

        mockMvc.perform(delete("/projects/"+projectForTests.getId()+"/topics/"+topicForTests.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isNoContent());

        int finalSize =  topicService.findAll(projectForTests.getId(),0,100000).size();

        Assertions.assertTrue(initialSize>finalSize);

    }

    @Test
    void createMessage() throws Exception {
        int initialSize = messageService.findAll(topicForTests.getId(),0,100000).size();
        mockMvc.perform(post("/projects/"+projectForTests.getId()+"/topics/"+topicForTests.getId()+"/messages")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content(this.messageJsonForTests))
                .andExpect(status().isCreated());

        int finalSize =  messageService.findAll(topicForTests.getId(),0,100000).size();

        Assertions.assertEquals(initialSize + 1, finalSize);
    }

    @Test
    void readAllMessagesOfATopic() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/"+projectForTests.getId()+"/topics/"
                +topicForTests.getId()+"/messages")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());

    }


    @Test
    void retrieveOneMessageOfATopic() throws Exception {

        ResultActions call = mockMvc.perform(get("/projects/"+projectForTests.getId()+"/topics/"
                +topicForTests.getId()+"/messages/"+messageForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));


        MvcResult returnValue = call.andReturn();
        System.out.println(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());

    }

    @Test
    void updateOneMessage() throws Exception {
        ResultActions call = mockMvc.perform(put("/projects/"+projectForTests.getId()+"/topics/"
                +topicForTests.getId()+"/messages/"+messageForTests.getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(messageJSonForTestsUpdated)
        );
        String result = call.andReturn().getResponse().getContentAsString();
        System.out.println(result);
        call.andExpect(status().isOk());
        Assertions.assertTrue(result.contains("UPDATED"));
    }

    @Test
    void deleteOneMessage() throws Exception {
        int initialSize = messageService.findAll(topicForTests.getId(),0,100000).size();
        System.err.println(initialSize);
        mockMvc.perform(delete("/projects/"+projectForTests.getId()+"/topics/"
                        +topicForTests.getId()+"/messages/"+messageForTests.getId())
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isNoContent());

        int finalSize =  messageService.findAll(topicForTests.getId(),0,100000).size();
        System.err.println(finalSize);
        Assertions.assertTrue(initialSize>finalSize);

    }

    @Test
    void countMessagesByTopic() throws Exception {
        int expectedNumber = messageService.findAll(topicForTests.getId(),0,100000).size();
        ResultActions call = mockMvc.perform(get("/projects/"+projectForTests.getId()+"/topics/"
                        +topicForTests.getId()+"/messages/count")
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk());

        MvcResult returnValue = call.andReturn();
        int obtainedNumber =Integer.parseInt(returnValue.getResponse().getContentAsString());
        call.andExpect(status().isOk());
        Assertions.assertEquals(expectedNumber,obtainedNumber);

    }


}
