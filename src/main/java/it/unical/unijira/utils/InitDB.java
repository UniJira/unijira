package it.unical.unijira.utils;

import it.unical.unijira.data.dao.projects.MembershipRepository;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.*;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemAssignment;
import it.unical.unijira.data.models.items.ItemStatus;
import it.unical.unijira.data.models.projects.Membership;
import it.unical.unijira.data.models.projects.MembershipKey;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.common.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@SpringBootApplication
public class InitDB implements CommandLineRunner {
  
  @Autowired
  private UserService userService;
  
  @Autowired
  private SprintService sprintService;
  
  @Autowired
  private ProductBacklogService productBacklogService;
  
  @Autowired
  private ProjectService projectService;
  
  @Autowired
  private MembershipRepository membershipRepository;
  
  @Autowired
  private ProductBacklogInsertionService productBacklogInsertionService;
  
  @Autowired
  private SprintInsertionService sprintInsertionService;
  
  @Autowired
  private ItemService itemService;
  
  @Autowired
  private ItemAssignmentService itemAssignmentService;
  
  private final int defaultPage = 0;
  private final int defaultSize = 10000;
  
  @Override
  public void run(String... args) throws Exception {
    if (userService.findAll().size() == 0) {
      log.info("Initializing database...");
      initUser();
      initEpic();
      initStory();
      initItem();
      initProject();
      initMembership();
      initBacklog();
      initSprint();
      addItemToBacklog();
      addItemToSprint();
      initItemAssignee();
      log.info("Database initialized");
    }
  }
  
  private void initStory() throws NonValidItemTypeException {
    log.info("Initializing Story...");
    User p = userService.findById(Long.parseLong("1")).orElseThrow();
  
    for (int i = 0; i < 5; i++) {
      Item item = new Item();
      item.setDescription("Story " + i);
      item.setTags("#tag1#,#tag2#");
      item.setOwner(p);
      item.setEvaluation(10 + i);
      item.setSummary("Summary Story " + i);
      item.setStatus(ItemStatus.OPEN);
      item.setMeasureUnit("Unit");
      item.setType("story");
      var randomEpicId = new Random().nextInt(3) ;
      var epic = itemService.findAllByType("epic").get(randomEpicId);
      item.setFather(epic);
      itemService.save(item);
    }
    log.info("Story initialized");
  }
  
  private void initEpic() throws NonValidItemTypeException {
    log.info("Initializing Epic...");
    User p = userService.findById(Long.parseLong("1")).orElseThrow();
  
    for (int i = 0; i < 3; i++) {
      Item item = new Item();
      item.setDescription("Epic " + i);
      item.setTags("#tag1#,#tag2#");
      item.setOwner(p);
      item.setEvaluation(10 + i);
      item.setSummary("Summary Epic " + i);
      item.setStatus(ItemStatus.OPEN);
      item.setMeasureUnit("story-points");
      item.setType("epic");
      itemService.save(item);
    }
    log.info("Epic initialized");
  }
  
  
  private void addItemToSprint() {
    log.info("Add item to Sprint...");
    User p = userService.findById(Long.parseLong("1")).orElseThrow();
    Project project = projectService.findAllByOwnerId(p.getId(), defaultPage, defaultSize).get(0);
    ProductBacklog backlog = productBacklogService.findAllByProject(project, defaultPage, defaultSize).get(0);
    Sprint sprint = sprintService.findSprintsByBacklog(backlog, defaultPage, defaultSize).get(0);
    List<Item> items = itemService.findAll().stream().limit(2).toList();
    for (Item item : items) {
      SprintInsertion insertion = new SprintInsertion();
      insertion.setItem(item);
      insertion.setSprint(sprint);
      sprintInsertionService.save(insertion);
    }
    log.info("Item added to Sprint");
  }
  
  private void addItemToBacklog() {
    log.info("Add item to Backlog...");
    
    User p = userService.findById(Long.parseLong("1")).orElseThrow();
    List<Project> project = projectService.findAllByOwnerId(p.getId(), defaultPage, defaultSize);
    List<ProductBacklog> backlog = productBacklogService.findAllByProject(project.get(0), defaultPage, defaultSize);
    List<Item> items = itemService.findAll();

    for (Item item : items) {
      ProductBacklogInsertion insertion = new ProductBacklogInsertion();
      insertion.setItem(item);
      insertion.setBacklog(backlog.get(0));
      insertion.setPriority(1);
      productBacklogInsertionService.save(insertion);
    }
    
    log.info("Item added to Backlog");
  }
  
  private void initSprint() {
    log.info("Initializing Sprint...");
    User p = userService.findById(Long.parseLong("1")).orElseThrow();
    List<Project> projects = projectService.findAllByOwnerId(Long.parseLong("1"), defaultPage, defaultSize);
    for (Project project : projects) {
      var projectsBacklogs = productBacklogService.findAllByProject(project, defaultPage, defaultSize);
      for (ProductBacklog backlog : projectsBacklogs) {
        Sprint sprint1 = new Sprint();
        Sprint sprint2 = new Sprint();
        Sprint sprint3 = new Sprint();
        sprint1.setBacklog(backlog);
        sprint2.setBacklog(backlog);
        sprint3.setBacklog(backlog);
        sprintService.save(sprint1);
        sprintService.save(sprint2);
        sprintService.save(sprint3);
      }
    }
    
    log.info("Sprint initialized");
  }
  
  private void initBacklog() {
    log.info("Initializing Backlog...");
    User p = userService.findById(Long.parseLong("1")).orElseThrow();
    List<Project> projects = projectService.findAllByOwnerId(Long.parseLong("1"), defaultPage, defaultSize);
    for (Project project : projects) {
      ProductBacklog backlog1 = new ProductBacklog();
      ProductBacklog backlog2 = new ProductBacklog();
      ProductBacklog backlog3 = new ProductBacklog();
      backlog1.setProject(project);
      backlog2.setProject(project);
      backlog3.setProject(project);
      productBacklogService.save(backlog1);
      productBacklogService.save(backlog2);
      productBacklogService.save(backlog3);
    }
    log.info("Backlog initialized");
  }
  
  private void initMembership() {
    log.info("Initializing Membership...");
    User p = userService.findById(Long.parseLong("1")).orElseThrow();
    List<Project> projects = projectService.findAllByOwnerId(Long.parseLong("1"), defaultPage, defaultSize);
    for (Project project : projects) {
      membershipRepository.save(new Membership(new MembershipKey(p, project), Membership.Role.MEMBER, Membership.Status.ENABLED, null));
    }
    log.info("Membership initialized");
  }
  
  private void initProject() throws MalformedURLException {
    log.info("Initializing Project...");
    if (projectService.findAllByOwnerId(Long.parseLong("1"), defaultPage, defaultSize).size() == 0) {
      User p = userService.findById(Long.parseLong("1")).orElseThrow();
      projectService.save(new Project(Long.parseLong("0"), "Project 1", "Project 1 key", new URL("https://firebasestorage.googleapis.com/v0/b/unijira-7b931.appspot.com/o/user%2F1%2Favatar%2FpuycMkQf_400x400.jpg?alt=media&token=2ba87976-65b8-44a1-9134-39f0f24579a5"), p, null, null));
      projectService.save(new Project(Long.parseLong("0"), "Project 2", "Project 2 key", new URL("https://testazzo.com"), p, null, null));
      projectService.save(new Project(Long.parseLong("0"), "Project 3", "Project 3 key", new URL("https://testazzo.com"), p, null, null));
    }
    log.info("Project initialized");
  }
  
  private void initItem() throws NonValidItemTypeException {
    log.info("Initializing Item...");
    User p = userService.findById(Long.parseLong("1")).orElseThrow();

    for (int i = 0; i < 10; i++) {
      Item item = new Item();
      item.setDescription("Item " + i);
      item.setTags("#tag1#,#tag2#");
      item.setOwner(p);
      item.setEvaluation(10 + i);
      item.setSummary("Summary " + i);
      item.setStatus(ItemStatus.OPEN);
//      var random = new Random().nextFloat();
//      System.out.println(random);
//      if (random > 0.7) {
//        item.setStatus(ItemStatus.OPEN);
//
//      } else {
//        item.setStatus(ItemStatus.DONE);
//      }
      item.setSummary("Summary " + i);
      item.setMeasureUnit("Unit");
      item.setType("task");
      itemService.save(item);
    }
    log.info("Item initialized");
  }
  
  private void initItemAssignee() {
    log.info("Initializing Item Assignee...");
    User p = userService.findById(Long.parseLong("1")).orElseThrow();
    List<Item> items = itemService.findAll();
    
    for (Item item : items) {
      ItemAssignment itemAssignment = new ItemAssignment();
      itemAssignment.setItem(item);
      itemAssignment.setAssignee(p);
      itemAssignmentService.save(itemAssignment);
      System.out.println(itemAssignment);
    }
    
    log.info("Item Assignee initialized");
  }
  
  private void initUser() throws MalformedURLException {
    log.info("Initializing User...");
    if (userService.findAll().size() == 0) {
      userService.save(new User(Long.parseLong("1"), "paolaguarasci@gmail.com", "Testazzo.99", true, false, new URL("https://eu.ui-avatars.com/api/?background=0D8ABC&color=fff&name=P+G"), null, null, null));
    }
    log.info("User initialized");
  }
  
}