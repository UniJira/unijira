package it.unical.unijira;


import it.unical.unijira.data.dao.*;
import it.unical.unijira.data.models.*;
import it.unical.unijira.utils.Config;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@Getter
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class UniJiraTest {

    protected final static String USERNAME = "unijira20@gmail.com";
    protected final static String PASSWORD = "Unijira.20";

    @Autowired
    protected Config config;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected NotifyRepository notifyRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserProjectRepository userProjectRepository;

    @Autowired
    protected ProductBacklogItemRepository pbiRepository;

    @Autowired
    protected ItemAssignmentRepository itemAssignmentRepository;

    @Autowired
    protected NoteRepository noteRepository;

    @Autowired
    protected MockMvc mockMvc;


    @BeforeEach
    public void init() {

        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(notifyRepository);

        if(userRepository.findByUsername(USERNAME).isEmpty()) {

            User user = User.builder()
                    .username(USERNAME)
                    .password(passwordEncoder.encode(PASSWORD))
                    .activated(true)
                    .ownedProjects(Collections.emptyList())
                    .memberships(Collections.emptyList())
                    .build();

            userRepository.saveAndFlush(user);


            Notify notify = Notify.builder()
                    .user(user)
                    .title("Test")
                    .message("Test")
                    .build();

            notifyRepository.saveAndFlush(notify);


            Project project = Project.builder()
                    .owner(user)
                    .name("Test")
                    .key("TST")
                    .memberships(Collections.emptyList())
                    .build();

            projectRepository.saveAndFlush(project);


            Membership membership = Membership.builder()
                    .status(Membership.Status.ENABLED)
                    .role(Membership.Role.SCRUM_MASTER)
                    .key(new MembershipKey(user, project))
                    .build();

            userProjectRepository.saveAndFlush(membership);

        }

    }


    public String performLogin(String username, String password) throws Exception {

        return mockMvc.perform(post("/auth/authenticate")
                .with(csrf())
                .contentType("application/json")
                .content("""
                        {
                            "username": "${USERNAME}",
                            "password": "${PASSWORD}"
                        }
                        """.replace("${USERNAME}", username).replace("${PASSWORD}", password))
        ).andReturn().getResponse().getContentAsString();

    }

}
