package it.unical.unijira.common;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AbstractBaseEntityTest extends UniJiraTest {

    @Test
    void updateUserTest() throws InterruptedException {

        Thread.sleep(100);

        var user = userRepository.findById(1L)
                .stream()
                .peek(i -> i.setStatus(User.Status.ACTIVE))
                .peek(userRepository::saveAndFlush)
                .findFirst()
                .orElseThrow(() -> new AssertionError("User not found"));


        Assertions.assertEquals(user.getStatus(), User.Status.ACTIVE);
        Assertions.assertNotNull(user.getCreatedAt());
        Assertions.assertNotNull(user.getUpdatedAt());

    }

}
