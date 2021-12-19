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

//    @Test
//    void DTOtoUserNotificationsTest() {
//
//        TestDTO testDTO = new TestDTO() {{
//
//            setNotificationsIds(notifyRepository.findAll()
//                    .stream()
//                    .mapToLong(Notify::getId)
//                    .boxed()
//                    .collect(Collectors.toList()));
//
//        }};
//
//
//        Assertions.assertNotNull(testDTO.getNotificationsIds());
//        Assertions.assertTrue(testDTO.getNotificationsIds().size() > 0);
//
//
//        Converter<List<Long>, List<Notify>> idToNotify = c -> c.getSource()
//                .stream()
//                .map(id -> new Notify() {{ setId(id); }})
//                .collect(Collectors.toList());
//
//
//        modelMapper.createTypeMap(TestDTO.class, User.class, "IdsToNotifications")
//                .addMappings(m -> m.using(idToNotify).map(TestDTO::getNotificationsIds, User::setNotifications));
//
//
//        User user = modelMapper.map(testDTO, User.class, "IdsToNotifications");
//
//        System.err.println(user.getNotifications().size());
//        user.getNotifications().forEach(System.err::println);
//
//        Assertions.assertNotNull(user.getNotifications());
//        Assertions.assertEquals(notifyRepository.count(), user.getNotifications().size());
//        Assertions.assertTrue(user.getNotifications().stream().allMatch(n -> n.getId() != null));
//        Assertions.assertTrue(user.getNotifications().stream().allMatch(n -> n.getMessage() != null));
//        Assertions.assertTrue(user.getNotifications().stream().allMatch(n -> n.getUser() != null));
//
//    }

}
