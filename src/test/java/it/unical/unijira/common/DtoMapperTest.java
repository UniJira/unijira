package it.unical.unijira.common;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.dto.NotifyDTO;
import it.unical.unijira.data.dto.user.ItemAssignmentDTO;
import it.unical.unijira.data.dto.user.ProductBacklogItemDTO;
import it.unical.unijira.data.dto.user.UserInfoDTO;
import it.unical.unijira.data.models.ItemAssignment;
import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DtoMapperTest extends UniJiraTest {

    @Autowired
    private ModelMapper modelMapper;


    @Test
    void notifyToDTOTest() {

        var notify = new Notify() {{
            setId(1L);
            setTitle("title");
            setMessage("message");
            setUser(new User() {{
                setId(2L);
                setUsername("username");
            }});
        }};

        NotifyDTO notifyDTO = modelMapper.map(notify, NotifyDTO.class);

        Assertions.assertEquals("title", notifyDTO.getTitle());
        Assertions.assertEquals("message", notifyDTO.getMessage());
        Assertions.assertEquals(2L, notifyDTO.getUserId());


    }

    @Test
    void DTOtoNotifyTest() {

        List<User> users = userRepository.findAll();

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
