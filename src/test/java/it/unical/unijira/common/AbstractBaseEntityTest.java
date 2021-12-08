package it.unical.unijira.common;

import it.unical.unijira.UniJiraTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AbstractBaseEntityTest extends UniJiraTest {

    @Test
    void updateUserTest() {

        var user = userRepository.findById(1L)
                .stream()
                .peek(i -> i.setActivated(true))
                .peek(userRepository::saveAndFlush)
                .findFirst()
                .orElseThrow(() -> new AssertionError("User not found"));

        Assertions.assertTrue(user.isActivated());
        Assertions.assertNotNull(user.getCreatedAt());
        Assertions.assertNotNull(user.getUpdatedAt());
        Assertions.assertNotEquals(user.getCreatedAt(), user.getUpdatedAt());

    }

}
