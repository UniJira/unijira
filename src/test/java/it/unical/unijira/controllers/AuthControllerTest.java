package it.unical.unijira.controllers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.models.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
public class AuthControllerTest extends UniJiraTest {

    @Value("${jwt.secret}")
    private String tokenSecret;


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
    void getMeSuccessful() throws Exception {

        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk());

    }

    @Test
    void getMeWithoutAuthorization() throws Exception {

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void getMeWithExpiredToken() throws Exception {

        var expiredToken = JWT.create()
                .withClaim("type", TokenType.AUTHORIZATION.name())
                .withExpiresAt(Date.from(Instant.now().minusSeconds(3600)))
                .sign(Algorithm.HMAC512(this.tokenSecret));

        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isIAmATeapot());

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