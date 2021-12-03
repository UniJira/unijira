package it.unical.unijira.controllers;


import it.unical.unijira.UniJiraTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
class AuthControllerTest extends UniJiraTest {

    @Test
    void authenticateSuccessful() throws Exception {


        mockMvc.perform(post("/auth/authenticate")
                .with(csrf())
                .contentType("application/json")
                .content("""
                        {
                            "username": "admin",
                            "password": "Admin123"
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
                            "username": "new-user2",
                            "password": "new-password"
                        }
                        """)
        ).andExpect(status().isBadRequest());

    }


}