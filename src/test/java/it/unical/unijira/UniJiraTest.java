package it.unical.unijira;


import it.unical.unijira.data.models.User.Role;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.UserService;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;


@Getter
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class UniJiraTest {

    @Autowired
    protected UserService userService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    public void init() {

        Assertions.assertNotNull(userService);

        if(userService.findByUsername("admin").isEmpty()) {

            User user = new User();
            user.setUsername("admin");
            user.setPassword("Admin123");

            userService.save(user);

        }

    }

}
