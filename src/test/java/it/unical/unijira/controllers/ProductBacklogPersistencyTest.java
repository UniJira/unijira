package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.*;
import it.unical.unijira.services.common.impl.ItemAssignmentServiceImpl;
import it.unical.unijira.services.common.impl.NoteServiceImpl;
import it.unical.unijira.services.common.impl.ProductBacklogItemServiceImpl;
import it.unical.unijira.services.common.impl.UserServiceImpl;
import it.unical.unijira.utils.ProductBacklogItemType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
public class ProductBacklogPersistencyTest extends UniJiraTest {


    @Autowired
    private ProductBacklogItemServiceImpl pbiService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private NoteServiceImpl noteService;

    @Autowired
    private ItemAssignmentServiceImpl itemAssignmentService;

    @BeforeEach
    public void initProductBacklogItem() {

        Assertions.assertNotNull(pbiRepository);
        Assertions.assertNotNull(noteRepository);
        Assertions.assertNotNull(itemAssignmentRepository);

        List<ProductBacklogItem> allMyItems = pbiRepository.findAll();

        if(allMyItems.isEmpty()) {


            ProductBacklogItem pbi = new ProductBacklogItem();
            try {
                pbi.setType(ProductBacklogItemType.getInstance().EPIC);
            } catch (NonValidItemTypeException e) {
               //Nothing
            }
            pbi.setSummary("THIS IS A SHORT DESCRIPTION");
            pbi.setDescription("THIS IS A SHORT DESCRIPTION, BUT LESS SHORT THAN THE PREVIOUS");
            pbi.setEvaluation(666);
            pbi.setMeasureUnit("metri");

            pbi = pbiService.save(pbi).get();


            Note note = new Note();
            note.setTimestamp(LocalDateTime.now());
            note.setRefersTo(pbi);
            note.setMessage("this is a dummy comment");
            note.setAuthor(new User());
            note.getAuthor().setId(1L);

            note = noteService.save(note).get();


            List<User> users = userRepository.findAll();
            for (User foundUser : users) {
                ItemAssignment assignment = new ItemAssignment();
                assignment.setAssignee(foundUser);
                assignment.setItem(pbi);
                itemAssignmentRepository.save(assignment);
            }






        }


    }

    @Test
    public void dummyTest(){

    }




}
