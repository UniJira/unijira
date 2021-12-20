package it.unical.unijira.common;

import it.unical.unijira.UniJiraApplication;
import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.dto.user.ItemAssignmentDTO;
import it.unical.unijira.data.dto.user.ItemDTO;
import it.unical.unijira.data.dto.user.UserInfoDTO;
import it.unical.unijira.data.models.Item;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.common.impl.UserServiceImpl;
import it.unical.unijira.utils.ItemType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest(classes = UniJiraApplication.class)
public class DtoMapperToProdBacklogItem extends UniJiraTest {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserServiceImpl userService;

    private List<Long> availableIdS = new ArrayList<>();

    @BeforeEach
    void addUser(){
        User userCiccio = new User();
        userCiccio.setUsername("ciccio");
        userCiccio.setPassword(passwordEncoder.encode(PASSWORD));
        userCiccio.setMemberships(Collections.emptyList());

        userRepository.saveAndFlush(userCiccio);
        List<User> users = userRepository.findAll();
        for (var user : users){
            availableIdS.add(user.getId());

        }
    }


    @Test
    void DTOToBacklogItemTest() {


        List<User> users = userRepository.findAll();


        var item = new ItemDTO() {{
            setId(1L);
            setSummary("item inutile");
            setDescription("questo item non serve assolutamente a nulla");
            setEvaluation(8);
            setType(ItemType.getInstance().EPIC);
            setMeasureUnit("metri");

        }};





        List<ItemAssignmentDTO> assignments = new ArrayList<>();
        if (availableIdS.size() > 1) {
            ItemAssignmentDTO firstAssignmentDTO = new ItemAssignmentDTO() {{
                this.setId(1L);
                this.setItem(item);
            }};
            UserInfoDTO firstAssignee = new UserInfoDTO() {{
                setId(availableIdS.get(1));
            }};
            firstAssignmentDTO.setAssignee(firstAssignee);
            assignments.add(firstAssignmentDTO);
        }
        if (availableIdS.size() > 0){
            ItemAssignmentDTO secondAssignmentDTO = new ItemAssignmentDTO() {{
                this.setId(2L);
                this.setItem(item);
            }};
            UserInfoDTO secondAssignee = new UserInfoDTO() {{
                setId(availableIdS.get(0));
            }};
            secondAssignmentDTO.setAssignee(secondAssignee);
            assignments.add(secondAssignmentDTO);
        }

        item.setAssignees(assignments);


        Item itemAssignment = modelMapper.map(item, Item.class);

        Assertions.assertNotNull(itemAssignment.getId());
        Assertions.assertNotNull(itemAssignment.getType());
        Assertions.assertNotNull(itemAssignment.getDescription());
        Assertions.assertNotNull(itemAssignment.getSummary());
        Assertions.assertNotNull(itemAssignment.getEvaluation());
        Assertions.assertNotNull(itemAssignment.getAssignees());
        Assertions.assertTrue(itemAssignment.getAssignees().size() > 0);
        Assertions.assertNotNull(itemAssignment.getCreatedAt());
        Assertions.assertNotNull(itemAssignment.getUpdatedAt());


    }
}
