package it.unical.unijira.controllers;


import com.auth0.jwt.JWT;
import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.models.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    void getMeSuccessful() throws Exception {

        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk());

    }

    @Test
    void getMeWithoutAuthorization() throws Exception {

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isForbidden());

    }

    @Test
    void getMeWithExpiredToken() throws Exception {

        var expiredToken = JWT.create()
                        .withIssuer(config.getJWTIssuer())
                        .withIssuedAt(Date.from(Instant.now().minusSeconds(config.getTokenExpiration() + 1)))
                        .withExpiresAt(Date.from(Instant.now().minusSeconds(config.getTokenExpiration() + 1)))
                        .withClaim("type", TokenType.AUTHORIZATION.toString())
                        .withClaim("username", UniJiraTest.USERNAME)
                        .withClaim("password", UniJiraTest.PASSWORD)
                        .sign(config.getJWTAlgorithm());


        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isIAmATeapot());

    }

    @Test
    void refreshExpiredToken() throws Exception {

        var expiredToken = JWT.create()
                .withIssuer(config.getJWTIssuer())
                .withIssuedAt(Date.from(Instant.now().minusSeconds(config.getTokenExpiration() + 1)))
                .withExpiresAt(Date.from(Instant.now().minusSeconds(config.getTokenExpiration() + 1)))
                .withClaim("type", TokenType.AUTHORIZATION.toString())
                .withClaim("username", UniJiraTest.USERNAME)
                .withClaim("password", UniJiraTest.PASSWORD)
                .sign(config.getJWTAlgorithm());


        mockMvc.perform(post("/auth/refresh").header("Authorization", "Bearer " + expiredToken)
                .contentType("application/json")
                .content("""
                        {
                            "token": "${expiredToken}"
                        }
                        """.replace("${expiredToken}", expiredToken))
                ).andExpect(status().isOk()).andDo(print());

    }

    @Test
    void registerSuccessful() throws Exception {

        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType("application/json")
                .content("""
                        {
                            "username": "deek47731@gmail.com",
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


    @Test
    void isUserAvailable() throws Exception {

        mockMvc.perform(get("/auth/available")
                .with(csrf())
                .param("username", "deek47731@gmail.com")
        ).andExpect(status().isOk());

        registerSuccessful();

        mockMvc.perform(get("/auth/available")
                .with(csrf())
                .param("username", "deek47731@gmail.com")
        ).andExpect(status().isConflict());

    }
}