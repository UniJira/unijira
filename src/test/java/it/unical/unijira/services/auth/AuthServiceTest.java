package it.unical.unijira.services.auth;

import it.unical.unijira.UniJiraTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class AuthServiceTest extends UniJiraTest {

    @Test
    void authenticateSuccessful() throws Exception {


        mockMvc.perform(post("/auth/authenticate")
                .with(csrf())
                .contentType("application/json")
                .content("""
                        {
                            "username": "admin",
                            "password": "admin"
                        }
                        """)
        ).andExpect(status().isOk());

    }

    @Test
    void authenticateWrongPassword() throws Exception {

        mockMvc.perform(post("/auth/authenticate")
                .with(csrf())
                .contentType("application/json")
                .content("""
                        {
                            "username": "admin",
                            "password": "wrong-password"
                        }
                        """)
        ).andExpect(status().isUnauthorized());

    }

    @Test
    void authenticateWrongUsername() throws Exception {

        mockMvc.perform(post("/auth/authenticate")
                .with(csrf())
                .contentType("application/json")
                .content("""
                        {
                            "username": "wrong-username-1234absc",
                            "password": "wrong-password"
                        }
                        """)
        ).andExpect(status().isUnauthorized());

    }

    @Test
    void register() {

    }

}