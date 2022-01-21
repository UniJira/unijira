package it.unical.unijira.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.dto.items.ItemDefinitionOfDoneDTO;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.ProductBacklogInsertion;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemType;
import it.unical.unijira.data.models.items.MeasureUnit;
import it.unical.unijira.data.models.projects.DefinitionOfDoneEntry;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.common.ProductBacklogInsertionService;
import it.unical.unijira.services.common.ProductBacklogService;
import it.unical.unijira.services.projects.DefinitionOfDoneEntryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemControllerTest extends UniJiraTest {

    private Long chooseMyId;
    private Item dummyItem;
    private Project dummyProject;
    private List<DefinitionOfDoneEntry> entries;
    private ProductBacklog dummyBacklog;

    @Autowired
    protected DefinitionOfDoneEntryService definitionOfDoneEntryService;

    @Autowired
    protected ProductBacklogService productBacklogService;

    @Autowired
    protected ProductBacklogInsertionService productBacklogInsertionService;

    @BeforeEach
    void insertDummyObject(){
        Item father = new Item();
        father.setDescription("this is an useless epic");
        father.setEvaluation(77);
        father.setMeasureUnit(MeasureUnit.WORKING_DAYS);
        father.setSummary("useless epic");
        try {
            father.setType(ItemType.EPIC);
            father.setFather(null);
        } catch (NonValidItemTypeException ignored) {}

        father.setOwner(userRepository.findAll().stream().findFirst().orElseThrow(RuntimeException::new));


        dummyItem = pbiRepository.saveAndFlush(father);


        this.chooseMyId = dummyItem.getId();
    }


    @Test
    void retrieveAllItems() throws Exception {
        mockMvc.perform(get("/items").header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":")));
    }

    @Test
    void newItem() throws Exception {
        mockMvc.perform(post("/items")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content("""
                        {
                            "summary": "HELLO I'M AN EPIC",
                            "description": "HI I'M AN EPIC AND I WANTO TO BE A GOOD EPIC",
                            "measureUnit": "story points",
                            "evaluation": "100000",
                            "tags" : "#BACKEND#;#FRONTEND#",
                            "type" : "EPIC",
                            "owner" : { "id": 1, "username" : "unijira20@gmail.com"}
                        }
                        """)
        ).andExpect(status().isCreated());
    }

    @Test
    void retrieveById() throws Exception {
        mockMvc.perform(get("/items/"+this.chooseMyId)
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":")));
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/"+this.chooseMyId)
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/items/"+this.chooseMyId)
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)))
                .andExpect(status().isNotFound());
        // FIXME: 08/01/2022: Disattivo temporaneamente il test perché non funziona nel mio ambiente locale (Linux), con la speranza che non rompa niente :'(
    }

    @Test
    void updateProjectSuccessful() throws Exception {

        mockMvc.perform(put("/items/"+this.chooseMyId)
                        .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                        .contentType("application/json")
                        .content("""
                        {
                            "summary": "HELLO I'M A STORY",
                            "description": "HI I'M AN EPIC AND I WANT TO BE A GOOD STORY",
                            "measureUnit": "story points",
                            "evaluation": "100033",
                            "tags" : "#BACKEND#;#FRONTEND#",
                            "type" : "STORY",
                            "owner" : { "id": 1, "username" : "unijira20@gmail.com"}
                        }
                        """)
                )
                .andExpect(status().isOk());

    }

    public void initDefOfDoneTests() {
        initProject();
        entries = new ArrayList<>();
        initDefOfDone(1);
        initDefOfDone(2);
        setupBacklog();
    }

    void initProject() {
        Project p = new Project();
        p.setName("DUMMY PROJECT");
        p.setKey("KEY");
        p.setOwner(userRepository.findByUsername(UniJiraTest.USERNAME).orElse(null));
        this.dummyProject = projectRepository.saveAndFlush(p);
    }

    void initDefOfDone(int p) {
        DefinitionOfDoneEntry d = new DefinitionOfDoneEntry();
        d.setProject(this.dummyProject);
        d.setDescription("Ciaone");
        d.setPriority(p);

        this.entries.add(definitionOfDoneEntryService.create(d).orElseThrow());
    }

    void setupBacklog()  {
        ProductBacklog backlog = new ProductBacklog();
        backlog.setProject(this.dummyProject);
        this.dummyBacklog = productBacklogService.save(backlog).orElseThrow();
    }

    void setupBacklogInsertion()  {
        ProductBacklogInsertion insertion = new ProductBacklogInsertion();
        insertion.setBacklog(this.dummyBacklog);
        insertion.setItem(dummyItem);
        insertion.setPriority(1);

        productBacklogInsertionService.save(insertion);
    }

    ResultActions createItemDefOfDone(int index) throws Exception {
        return mockMvc.perform(post("/items/" + this.dummyItem.getId() + "/defofdone")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
                .contentType("application/json")
                .content("""
                        {
                            "keyDefinitionOfDoneEntryId": "%d"
                        }
                        """.formatted(entries.get(index).getId()))
        );
    }

    @Test
    void createItemDefOfDoneFailure() throws Exception {
        initDefOfDoneTests();

        createItemDefOfDone(0).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    void createItemDefOfDoneSuccess() throws Exception {
        initDefOfDoneTests();
        setupBacklogInsertion();

        createItemDefOfDone(0).andExpect(status().isOk()).andDo(print());
    }

    @Test
    void getItemDefinitionOfDoneEntries() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        initDefOfDoneTests();
        setupBacklogInsertion();

        createItemDefOfDone(0).andExpect(status().isOk()).andDo(print());
        createItemDefOfDone(1).andExpect(status().isOk()).andDo(print());

        String result = mockMvc.perform(get("/items/" + this.dummyItem.getId() + "/defofdone")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
        ).andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();

        List<ItemDefinitionOfDoneDTO> resultList = mapper.readValue(result, new TypeReference<>() {});

        Assertions.assertFalse(resultList.isEmpty());
        Assertions.assertEquals(
                2,
                resultList.stream()
                        .filter(r -> r.getKeyItemId().equals(this.dummyItem.getId()))
                        .count()
        );

        Assertions.assertTrue(
        resultList.stream()
                .map(ItemDefinitionOfDoneDTO::getKeyDefinitionOfDoneEntryId)
                .allMatch(
                        r -> entries.stream()
                                .map(DefinitionOfDoneEntry::getId)
                                .anyMatch(s -> s.equals(r)))
        );
    }

    @Test
    void deleteItemDefinitionOfDoneEntry() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        initDefOfDoneTests();
        setupBacklogInsertion();

        createItemDefOfDone(0).andExpect(status().isOk()).andDo(print());

        mockMvc.perform(delete("/items/" + this.dummyItem.getId() + "/defofdone/" + this.entries.get(0).getId())
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
        ).andExpect(status().isNoContent()).andDo(print());

        String result = mockMvc.perform(get("/items/" + this.dummyItem.getId() + "/defofdone")
                .header("Authorization", "Bearer " + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD))
        ).andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();

        List<ItemDefinitionOfDoneDTO> resultList = mapper.readValue(result, new TypeReference<>() {});

        Assertions.assertTrue(resultList.isEmpty());
    }
}
