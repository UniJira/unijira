package it.unical.unijira;


import it.unical.unijira.data.dao.NotifyRepository;
import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.User;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@Getter
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class UniJiraTest {

    protected final static String USERNAME = "unijira20@gmail.com";
    protected final static String PASSWORD = "Unijira20";

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected NotifyRepository notifyRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected MockMvc mockMvc;


    @BeforeEach
    public void init() {

        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(notifyRepository);

        if(userRepository.findByUsername(USERNAME).isEmpty()) {

            User user = new User();
            user.setUsername(USERNAME);
            user.setPassword(passwordEncoder.encode(PASSWORD));

            userRepository.saveAndFlush(user);


            Notify notify = new Notify();
            notify.setUser(user);
            notify.setTitle("Test");
            notify.setMessage("Test");

            notifyRepository.saveAndFlush(notify);

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
