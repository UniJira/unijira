package it.unical.unijira.common;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.dto.NotifyDTO;
import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.User;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
public class DtoMapperTest extends UniJiraTest {

    @Autowired
    private ModelMapper modelMapper;

    @Getter
    @Setter
    private static class TestDTO {
        private List<Long> notificationsIds;
    }


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

    @Test
    void DTOtoEntityDateTest() {

        LocalDateTime date = LocalDateTime.of(2021, Month.JANUARY, 1, 16, 30, 30, 0);

        var notifyDTO = new NotifyDTO() {{
            setId(null);
            setCreatedAt(date.format(DateTimeFormatter.ISO_DATE_TIME));
            setUpdatedAt(date.format(DateTimeFormatter.ISO_DATE_TIME));
        }};

        Assertions.assertEquals(date.format(DateTimeFormatter.ISO_DATE_TIME), notifyDTO.getCreatedAt());
        Assertions.assertEquals(date.format(DateTimeFormatter.ISO_DATE_TIME), notifyDTO.getUpdatedAt());


        Notify notify = modelMapper.map(notifyDTO, Notify.class);

        Assertions.assertEquals("2021-01-01T16:30:30", date.format(DateTimeFormatter.ISO_DATE_TIME));
        Assertions.assertEquals(date, notify.getCreatedAt());
        Assertions.assertEquals(date, notify.getUpdatedAt());

    }

    @Test
    void EntityToDTODateTest() {

        LocalDateTime date = LocalDateTime.of(2021, Month.JANUARY, 1, 16, 30, 30, 0);

        var notify = new Notify() {{
            setId(null);
            setCreatedAt(date);
            setUpdatedAt(date);
        }};

        Assertions.assertEquals(date, notify.getCreatedAt());
        Assertions.assertEquals(date, notify.getUpdatedAt());


        NotifyDTO notifyDTO = modelMapper.map(notify, NotifyDTO.class);

        Assertions.assertEquals("2021-01-01T16:30:30", date.format(DateTimeFormatter.ISO_DATE_TIME));
        Assertions.assertEquals(date.format(DateTimeFormatter.ISO_DATE_TIME), notifyDTO.getCreatedAt());
        Assertions.assertEquals(date.format(DateTimeFormatter.ISO_DATE_TIME), notifyDTO.getUpdatedAt());

    }

}
