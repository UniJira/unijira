package it.unical.unijira.controllers.discussionboard;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.common.ProjectService;
import it.unical.unijira.services.common.UserService;
import it.unical.unijira.services.discussionboard.MessageService;
import it.unical.unijira.services.discussionboard.TopicService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TopicControllerTest extends UniJiraTest {

    private Project projectForTests;
    private User userForTests;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    private String topicJsonForTests;

    public TopicControllerTest() {

    }

    @BeforeEach
    void setupAllStuff() throws Exception {
        Project p = new Project();
        p.setName("DUMMY PROJECT");
        p.setKey("KEY");
        p.setOwner(userRepository.findByUsername(UniJiraTest.USERNAME).orElse(null));
        this.projectForTests = projectRepository.saveAndFlush(p);
        this.userForTests = userService.findByUsername(UniJiraTest.USERNAME).orElse(null);
        this.setupTopic();
        this.setupMessage();
    }

    private void setupMessage() {
    }

    private void setupTopic() {

        this.topicJsonForTests =  "{\n" +
                "                         \t\"summary\" : \"This is the most wonderful topic you can see in this project\",\n" +
                "                         \t\"projectId\" : \""+projectForTests.getId()+"\",\n" +
                "                         \t\"userId\" : \"" + this.userForTests.getId() + "\",\n" +
                "                         \t\"messages\" : []\n" +
                "                         \t}\n" +
                "                         }";

    }

    @Test
    void createTopic() throws Exception{
/*
        int initialSize = topicService.findAll(projectForTests.getId(),0,100000).size();
        System.err.println(initialSize);
        mockMvc.perform(post("/projects/"+projectForTests.getId()+"/topics")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content(this.topicJsonForTests))
                .andExpect(status().isCreated());

        int finalSize =  topicService.findAll(projectForTests.getId(),0,100000).size();
        System.err.println(finalSize);
        Assertions.assertTrue(initialSize+1==finalSize);


 */
    }


}
