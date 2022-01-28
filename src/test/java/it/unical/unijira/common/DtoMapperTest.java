package it.unical.unijira.common;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.dto.NotifyDTO;
import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;
import java.net.URL;

@SpringBootTest
public class DtoMapperTest extends UniJiraTest {

    @Autowired
    private ModelMapper modelMapper;


    @Test
    void notifyToDTOTest() throws MalformedURLException {

        var notify = Notify.builder()
                .id(1L)
                .title("title")
                .message("message")
                .target(new URL("http://www.google.com"))
                .user(User.builder()
                        .id(2L)
                        .username("username")
                        .build())
                .build();


        NotifyDTO notifyDTO = modelMapper.map(notify, NotifyDTO.class);

        Assertions.assertEquals("title", notifyDTO.getTitle());
        Assertions.assertEquals("message", notifyDTO.getMessage());
        Assertions.assertEquals(2L, notifyDTO.getUserId());
        Assertions.assertEquals("http://www.google.com", notifyDTO.getTarget().toString());


    }

    @Test
    void DTOtoNotifyTest() {

        var notifyDTO = new NotifyDTO() {{
            setTitle("title");
            setMessage("message");
            setUserId(1L);
        }};

        Notify notify = modelMapper.map(notifyDTO, Notify.class);

        Assertions.assertEquals("title", notify.getTitle());
        Assertions.assertEquals("message", notify.getMessage());
        Assertions.assertEquals(1L, notify.getUser().getId());
        Assertions.assertNotNull(notify.getUser().getUsername());
        Assertions.assertNotNull(notify.getUser().getPassword());

    }

}
