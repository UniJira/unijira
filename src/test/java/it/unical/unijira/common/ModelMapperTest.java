package it.unical.unijira.common;

import it.unical.unijira.data.dto.NotifyDTO;
import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ModelMapperTest {

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

}
