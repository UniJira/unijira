package it.unical.unijira.controllers;


import it.unical.unijira.UniJiraTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
public class AuthControllerTest extends UniJiraTest {

    @Test
    void authenticateSuccessful() throws Exception {
        Assertions.assertFalse(this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD).isBlank());
    }

    @Test
    void authenticateWrongPassword() throws Exception {
        Assertions.assertTrue(this.performLogin(UniJiraTest.USERNAME, "wrong-password-123").isBlank());
    }

    @Test
    void authenticateWrongUsername() throws Exception {
        Assertions.assertTrue(this.performLogin("wrong-username-123", "wrong-password-123").isBlank());
    }

    @Test
    void registerSuccessful() throws Exception {

        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType("application/json")
                .content("""
                        {
                            "username": "antonio.natale97@hotmail.com",
                            "password": "new-password123ABC"
                        }
                        """)
        ).andExpect(status().isCreated());

    }

    @Test
    void registerWrongPassword() throws Exception {

        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType("application/json")
                .content("""
                        {
                            "username": "new-user2@wrong.com",
                            "password": "new-password"
                        }
                        """)
        ).andExpect(status().isBadRequest());

    }


}