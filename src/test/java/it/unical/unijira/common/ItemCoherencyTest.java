package it.unical.unijira.common;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.*;
import it.unical.unijira.data.models.items.*;
import it.unical.unijira.data.models.projects.DefinitionOfDoneEntry;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.common.*;
import it.unical.unijira.services.projects.DefinitionOfDoneEntryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ItemCoherencyTest extends UniJiraTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProductBacklogService productBacklogService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ProductBacklogInsertionService productBacklogInsertionService;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private SprintInsertionService sprintInsertionService;

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private RoadmapInsertionService roadmapInsertionService;

    @Autowired
    private DefinitionOfDoneEntryService definitionOfDoneEntryService;

    @Autowired
    private ItemDefinitionOfDoneService itemDefinitionOfDoneService;


    private String token;
    private User userForTests;
    private List<Project> projects;
    private Item item;
    private List<ProductBacklog> productBacklogs;
    private List<Sprint> sprints;
    private List<DefinitionOfDoneEntry> definitionOfDoneEntries;
    private List<Roadmap> roadmaps;


    @BeforeEach
    void initInitialScenario() throws Exception {
        if (token == null) {
            token = this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD);
        }
        if (userForTests == null) {
            userForTests = userRepository.findByUsername(UniJiraTest.USERNAME).orElse(null);
        }

        initProjects();
        item = createItem();
        initDefOfDone();
        initBacklogs();
        initSprints();
        initRoadmaps();
    }

    void initProjects() {
        projects = new ArrayList<>();

        Project p = new Project();
        p.setName("DUMMY PROJECT 1");
        p.setKey("PR1");
        p.setOwner(userForTests);
        projects.add(projectService.create(p).orElseThrow());

        p = new Project();
        p.setName("DUMMY PROJECT 2");
        p.setKey("PR2");
        p.setOwner(userForTests);
        projects.add(projectService.create(p).orElseThrow());

        Assertions.assertEquals(2, projects.size());
    }

    Item createItem() {
        Item i = new Item();

        i.setDescription("this is an useless epic");
        i.setEvaluation(77);
        i.setMeasureUnit(MeasureUnit.WORKING_DAYS);
        i.setSummary("useless epic");

        try {
            i.setType(ItemType.EPIC);
            i.setFather(null);
        } catch (NonValidItemTypeException ignored) {}

        i.setOwner(userForTests);

        i = itemService.save(i).orElse(null);

        Assertions.assertNotNull(i);

        return i;
    }

    void initDefOfDone() {
        definitionOfDoneEntries = new ArrayList<>();

        DefinitionOfDoneEntry d = new DefinitionOfDoneEntry();
        d.setProject(projects.get(0));
        d.setDescription("DOD1");
        d.setPriority(1);
        definitionOfDoneEntries.add(definitionOfDoneEntryService.create(d).orElseThrow());

        d = new DefinitionOfDoneEntry();
        d.setProject(projects.get(1));
        d.setDescription("DOD2");
        d.setPriority(1);
        definitionOfDoneEntries.add(definitionOfDoneEntryService.create(d).orElseThrow());

        Assertions.assertEquals(2, definitionOfDoneEntries.size());
    }

    void initBacklogs()  {
        productBacklogs = new ArrayList<>();

        ProductBacklog backlog = new ProductBacklog();
        backlog.setProject(projects.get(0));
        productBacklogs.add(productBacklogService.save(backlog).orElseThrow());

        backlog = new ProductBacklog();
        backlog.setProject(projects.get(1));
        productBacklogs.add(productBacklogService.save(backlog).orElseThrow());

        Assertions.assertEquals(2, productBacklogs.size());
    }

    void initSprints()  {
        sprints = new ArrayList<>();

        Sprint s = new Sprint();
        s.setBacklog(productBacklogs.get(0));
        s.setStartingDate(LocalDate.now());
        s.setEndingDate(LocalDate.of(2022,1,31));
        sprints.add(sprintService.save(s).orElseThrow());

        s = new Sprint();
        s.setBacklog(productBacklogs.get(1));
        s.setStartingDate(LocalDate.now());
        s.setEndingDate(LocalDate.of(2022,1,31));
        sprints.add(sprintService.save(s).orElseThrow());

        Assertions.assertEquals(2, sprints.size());
    }

    void initRoadmaps()  {
        roadmaps = new ArrayList<>();

        Roadmap r = new Roadmap();
        r.setBacklog(productBacklogs.get(0));
        roadmaps.add(roadmapService.save(r).orElseThrow());

        r = new Roadmap();
        r.setBacklog(productBacklogs.get(1));
        roadmaps.add(roadmapService.save(r).orElseThrow());

        Assertions.assertEquals(2, roadmaps.size());
    }

    Optional<ProductBacklogInsertion> addBacklogInsertion(int backlogIndex)  {
        ProductBacklogInsertion insertion = new ProductBacklogInsertion();
        insertion.setBacklog(productBacklogs.get(backlogIndex));
        insertion.setItem(item);
        insertion.setPriority(1);

        return productBacklogInsertionService.save(insertion);
    }

    Optional<SprintInsertion> addSprintInsertion(int sprintIndex)  {
        SprintInsertion insertion = new SprintInsertion();
        insertion.setSprint(sprints.get(sprintIndex));
        insertion.setItem(item);

        return sprintInsertionService.save(insertion);
    }

    Optional<RoadmapInsertion> addRoadmapInsertion(int roadmapIndex)  {
        RoadmapInsertion insertion = new RoadmapInsertion();
        insertion.setRoadmap(roadmaps.get(roadmapIndex));
        insertion.setItem(item);
        insertion.setStartingDate(LocalDate.now());
        insertion.setEndingDate(LocalDate.of(2022,1,31));

        return roadmapInsertionService.save(insertion);
    }

    Optional<ItemDefinitionOfDone> addDefOfDoneInsertion(int entryIndex)  {
        ItemDefinitionOfDone insertion = new ItemDefinitionOfDone();
        insertion.setKey(new ItemDefinitionOfDoneKey(
                definitionOfDoneEntries.get(entryIndex),
                item
        ));

        return itemDefinitionOfDoneService.create(insertion);
    }



    @Test
    void addBacklogInsertionSuccess() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
    }

    @Test
    void addBacklogInsertionFailure() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        Assertions.assertNull(addBacklogInsertion(1).orElse(null));
    }

    @Test
    void updateBacklogInsertionSuccess() {
        ProductBacklogInsertion first = addBacklogInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        ProductBacklog anotherBacklog = new ProductBacklog();
        anotherBacklog.setProject(projects.get(0));
        anotherBacklog = productBacklogService.save(anotherBacklog).orElse(null);
        Assertions.assertNotNull(anotherBacklog);

        first.setBacklog(anotherBacklog);
        Assertions.assertNotNull(productBacklogInsertionService.update(first.getId(), first).orElse(null));
    }



    @Test
    void addSprintInsertionSuccess() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        Assertions.assertNotNull(addSprintInsertion(0).orElse(null));

        Sprint anotherSprint = new Sprint();
        anotherSprint.setBacklog(productBacklogs.get(0));
        anotherSprint.setStartingDate(LocalDate.now());
        anotherSprint.setEndingDate(LocalDate.of(2022,1,31));
        sprints.add(sprintService.save(anotherSprint).orElse(null));

        Assertions.assertNotNull(sprints.get(2));

        Assertions.assertNotNull(addSprintInsertion(2).orElse(null));
    }

    @Test
    void addSprintInsertionFailureSameItemAndSprint() {
        addSprintInsertionSuccess();
        Assertions.assertNull(addSprintInsertion(0).orElse(null));
    }

    @Test
    void addSprintInsertionFailureDifferentBacklog() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        Assertions.assertNull(addSprintInsertion(1).orElse(null));
    }

    @Test
    void addSprintInsertionFailureNoBacklog() {
        Assertions.assertNull(addSprintInsertion(0).orElse(null));
    }

    @Test
    void updateSprintInsertionSuccess() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        SprintInsertion first = addSprintInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        Sprint anotherSprint = new Sprint();
        anotherSprint.setBacklog(productBacklogs.get(0));
        anotherSprint.setStartingDate(LocalDate.now());
        anotherSprint.setEndingDate(LocalDate.of(2022,1,31));
        anotherSprint = sprintService.save(anotherSprint).orElse(null);

        Assertions.assertNotNull(anotherSprint);

        first.setItem(null);
        first.setSprint(anotherSprint);
        Assertions.assertNotNull(sprintInsertionService.update(first.getId(), first).orElse(null));
    }

    @Test
    void updateSprintInsertionSuccessItemChange() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        SprintInsertion first = addSprintInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        item = createItem();
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));

        first.setItem(item);
        first.setSprint(null);
        Assertions.assertNotNull(sprintInsertionService.update(first.getId(), first).orElse(null));
    }

    @Test
    void updateSprintInsertionSuccessItemAndSprintChange() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        SprintInsertion first = addSprintInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        item = createItem();
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));

        Sprint anotherSprint = new Sprint();
        anotherSprint.setBacklog(productBacklogs.get(0));
        anotherSprint.setStartingDate(LocalDate.now());
        anotherSprint.setEndingDate(LocalDate.of(2022,1,31));
        anotherSprint = sprintService.save(anotherSprint).orElse(null);

        Assertions.assertNotNull(anotherSprint);

        first.setItem(item);
        first.setSprint(anotherSprint);
        Assertions.assertNotNull(sprintInsertionService.update(first.getId(), first).orElse(null));
    }

    @Test
    void updateSprintInsertionFailure() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        SprintInsertion first = addSprintInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        Sprint anotherSprint = new Sprint();
        anotherSprint.setBacklog(productBacklogs.get(1));
        anotherSprint.setStartingDate(LocalDate.now());
        anotherSprint.setEndingDate(LocalDate.of(2022,1,31));
        anotherSprint = sprintService.save(anotherSprint).orElse(null);

        Assertions.assertNotNull(anotherSprint);

        first.setItem(null);
        first.setSprint(anotherSprint);
        Assertions.assertNull(sprintInsertionService.update(first.getId(), first).orElse(null));
    }

    @Test
    void updateSprintInsertionFailureItemChange() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        SprintInsertion first = addSprintInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        item = createItem();
        Assertions.assertNotNull(addBacklogInsertion(1).orElse(null));

        first.setItem(item);
        first.setSprint(null);
        Assertions.assertNull(sprintInsertionService.update(first.getId(), first).orElse(null));
    }

    @Test
    void updateSprintInsertionFailureItemAndSprintChange() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        SprintInsertion first = addSprintInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        item = createItem();

        Sprint anotherSprint = new Sprint();
        anotherSprint.setBacklog(productBacklogs.get(1));
        anotherSprint.setStartingDate(LocalDate.now());
        anotherSprint.setEndingDate(LocalDate.of(2022,1,31));
        anotherSprint = sprintService.save(anotherSprint).orElse(null);

        Assertions.assertNotNull(anotherSprint);

        first.setItem(item);
        first.setSprint(anotherSprint);
        Assertions.assertNull(sprintInsertionService.update(first.getId(), first).orElse(null));
    }



    @Test
    void addRoadmapInsertionSuccess() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        Assertions.assertNotNull(addRoadmapInsertion(0).orElse(null));

        Roadmap anotherRoadmap = new Roadmap();
        anotherRoadmap.setBacklog(productBacklogs.get(0));
        roadmaps.add(roadmapService.save(anotherRoadmap).orElse(null));

        Assertions.assertNotNull(roadmaps.get(2));

        Assertions.assertNotNull(addRoadmapInsertion(2).orElse(null));
    }

    @Test
    void addRoadmapInsertionFailureSameItemAndRoadmap() {
        addRoadmapInsertionSuccess();
        Assertions.assertNull(addRoadmapInsertion(0).orElse(null));
    }

    @Test
    void addRoadmapInsertionFailureDifferentBacklog() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        Assertions.assertNull(addRoadmapInsertion(1).orElse(null));
    }

    @Test
    void addRoadmapInsertionFailureNoBacklog() {
        Assertions.assertNull(addRoadmapInsertion(0).orElse(null));
    }

    @Test
    void updateRoadmapInsertionSuccess() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        RoadmapInsertion first = addRoadmapInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        Roadmap anotherRoadmap = new Roadmap();
        anotherRoadmap.setBacklog(productBacklogs.get(0));
        anotherRoadmap = roadmapService.save(anotherRoadmap).orElse(null);

        Assertions.assertNotNull(anotherRoadmap);

        first.setItem(null);
        first.setRoadmap(anotherRoadmap);
        Assertions.assertNotNull(roadmapInsertionService.update(first.getId(), first).orElse(null));
    }

    @Test
    void updateRoadmapInsertionSuccessItemChange() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        RoadmapInsertion first = addRoadmapInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        item = createItem();
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));

        first.setItem(item);
        first.setRoadmap(null);
        Assertions.assertNotNull(roadmapInsertionService.update(first.getId(), first).orElse(null));
    }

    @Test
    void updateRoadmapInsertionSuccessItemAndRoadmapChange() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        RoadmapInsertion first = addRoadmapInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        item = createItem();
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));

        Roadmap anotherRoadmap = new Roadmap();
        anotherRoadmap.setBacklog(productBacklogs.get(0));
        anotherRoadmap = roadmapService.save(anotherRoadmap).orElse(null);

        Assertions.assertNotNull(anotherRoadmap);

        first.setItem(item);
        first.setRoadmap(anotherRoadmap);
        Assertions.assertNotNull(roadmapInsertionService.update(first.getId(), first).orElse(null));
    }

    /*@Test
    void updateRoadmapInsertionFailure() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        RoadmapInsertion first = addRoadmapInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        Roadmap anotherRoadmap = new Roadmap();
        anotherRoadmap.setBacklog(productBacklogs.get(1));
        anotherRoadmap = roadmapService.save(anotherRoadmap).orElse(null);

        Assertions.assertNotNull(anotherRoadmap);

        first.setItem(null);
        first.setRoadmap(anotherRoadmap);
        Assertions.assertNull(roadmapInsertionService.update(first.getId(), first).orElse(null));
    }

    @Test
    void updateRoadmapInsertionFailureItemChange() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        RoadmapInsertion first = addRoadmapInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        item = createItem();
        Assertions.assertNotNull(addBacklogInsertion(1).orElse(null));

        first.setItem(item);
        first.setRoadmap(null);
        Assertions.assertNull(roadmapInsertionService.update(first.getId(), first).orElse(null));
    }

    @Test
    void updateRoadmapInsertionFailureItemAndRoadmapChange() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        RoadmapInsertion first = addRoadmapInsertion(0).orElse(null);
        Assertions.assertNotNull(first);

        item = createItem();

        Roadmap anotherRoadmap = new Roadmap();
        anotherRoadmap.setBacklog(productBacklogs.get(1));
        anotherRoadmap = roadmapService.save(anotherRoadmap).orElse(null);

        Assertions.assertNotNull(anotherRoadmap);

        first.setItem(item);
        first.setRoadmap(anotherRoadmap);
        Assertions.assertNull(roadmapInsertionService.update(first.getId(), first).orElse(null));
    }*/

    @Test
    void addDefOfDoneEntrySuccess() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));
        Assertions.assertNotNull(addDefOfDoneInsertion(0).orElse(null));
    }

    @Test
    void addDefOfDoneEntryFailureNoBacklog() {
        Assertions.assertNull(addDefOfDoneInsertion(0).orElse(null));
    }

    @Test
    void addDefOfDoneEntryFailureWrongProject() {
        Assertions.assertNotNull(addBacklogInsertion(0).orElse(null));

        Assertions.assertNotNull(addDefOfDoneInsertion(0).orElse(null));
        Assertions.assertNull(addDefOfDoneInsertion(1).orElse(null));
    }



    @Test
    void removeBacklogInsertion() {
        ProductBacklogInsertion productBacklogInsertion = addBacklogInsertion(0).orElse(null);
        Assertions.assertNotNull(productBacklogInsertion);

        SprintInsertion sprintInsertion = addSprintInsertion(0).orElse(null);
        Assertions.assertNotNull(sprintInsertion);

        RoadmapInsertion roadmapInsertion = addRoadmapInsertion(0).orElse(null);
        Assertions.assertNotNull(roadmapInsertion);

        ItemDefinitionOfDone itemDefinitionOfDone = addDefOfDoneInsertion(0).orElse(null);
        Assertions.assertNotNull(itemDefinitionOfDone);

        productBacklogInsertionService.delete(productBacklogInsertion);

        Assertions.assertNull(productBacklogInsertionService.findById(productBacklogInsertion.getId()).orElse(null));
        Assertions.assertNull(itemDefinitionOfDoneService.findById(item.getId(), definitionOfDoneEntries.get(0).getId()).orElse(null));
        Assertions.assertNull(sprintInsertionService.findById(sprintInsertion.getId()).orElse(null));
        Assertions.assertNull(roadmapInsertionService.findById(roadmapInsertion.getId()).orElse(null));
    }

    @Test
    void updateBacklogInsertionDifferentProject() {
        ProductBacklogInsertion productBacklogInsertion = addBacklogInsertion(0).orElse(null);
        Assertions.assertNotNull(productBacklogInsertion);

        SprintInsertion sprintInsertion = addSprintInsertion(0).orElse(null);
        Assertions.assertNotNull(sprintInsertion);

        RoadmapInsertion roadmapInsertion = addRoadmapInsertion(0).orElse(null);
        Assertions.assertNotNull(roadmapInsertion);

        ItemDefinitionOfDone itemDefinitionOfDone = addDefOfDoneInsertion(0).orElse(null);
        Assertions.assertNotNull(itemDefinitionOfDone);

        productBacklogInsertion.setBacklog(productBacklogs.get(1));
        Assertions.assertNotNull(productBacklogInsertionService.update(productBacklogInsertion.getId(), productBacklogInsertion).orElse(null));

        Assertions.assertNull(itemDefinitionOfDoneService.findById(item.getId(), definitionOfDoneEntries.get(0).getId()).orElse(null));
        Assertions.assertNull(sprintInsertionService.findById(sprintInsertion.getId()).orElse(null));
        Assertions.assertNull(roadmapInsertionService.findById(roadmapInsertion.getId()).orElse(null));
    }

    @Test
    void updateBacklogInsertionSameProject() {
        ProductBacklogInsertion productBacklogInsertion = addBacklogInsertion(0).orElse(null);
        Assertions.assertNotNull(productBacklogInsertion);

        SprintInsertion sprintInsertion = addSprintInsertion(0).orElse(null);
        Assertions.assertNotNull(sprintInsertion);

        RoadmapInsertion roadmapInsertion = addRoadmapInsertion(0).orElse(null);
        Assertions.assertNotNull(roadmapInsertion);

        ItemDefinitionOfDone itemDefinitionOfDone = addDefOfDoneInsertion(0).orElse(null);
        Assertions.assertNotNull(itemDefinitionOfDone);

        ProductBacklog anotherBacklog = new ProductBacklog();
        anotherBacklog.setProject(projects.get(0));
        anotherBacklog = productBacklogService.save(anotherBacklog).orElse(null);
        Assertions.assertNotNull(anotherBacklog);

        productBacklogInsertion.setBacklog(anotherBacklog);
        Assertions.assertNotNull(productBacklogInsertionService.update(productBacklogInsertion.getId(), productBacklogInsertion).orElse(null));

        Assertions.assertNotNull(itemDefinitionOfDoneService.findById(item.getId(), definitionOfDoneEntries.get(0).getId()).orElse(null));
        Assertions.assertNull(sprintInsertionService.findById(sprintInsertion.getId()).orElse(null));
        Assertions.assertNull(roadmapInsertionService.findById(roadmapInsertion.getId()).orElse(null));
    }

}
