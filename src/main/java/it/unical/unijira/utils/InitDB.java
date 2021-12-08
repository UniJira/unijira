package it.unical.unijira.utils;

import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.Project;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.common.NotifyService;
import it.unical.unijira.services.common.ProjectService;
import it.unical.unijira.services.common.UserService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.util.List;

@SpringBootApplication
@Builder
@Slf4j
public class InitDB  implements CommandLineRunner {
  
  private final UserService userService;
  private final NotifyService notifyService;
  private final ProjectService projectService;
  
  @Override
  public void run(String... args) throws Exception {
    log.info("ApplicationStartupRunner run method Started !!");
    initUsers();
    initNotify();
    initProjects();
    log.info("ApplicationStartupRunner run method Ended !!");
  }
  
  private void initProjects() {
    List<Project> projects = projectService.findAll().orElse(null);
    List<User> users = userService.findAll().orElse(null);
    if (projects == null || projects.isEmpty()) {
      log.info("No Project found in DB, adding some default Projects");
      assert users != null;
      users.forEach(user -> {
        projectService.create(Project.builder().name("Project " + user.getId()).key("").memberships(null).owner(user).build());
      });
    } else {
      projects.forEach(project -> log.info("Project found in DB: {}", project));
      log.info("Projects already exist in the database, doing nothing");
    }
  }
  
  private void initNotify() {
    List<Notify> notifies = notifyService.findAll().orElse(null);
    List<User> users = userService.findAll().orElse(null);
    if (notifies == null || notifies.isEmpty()) {
      log.info("No Notify found in DB, adding some default Notify");
      assert users != null;
      users.forEach(user -> notifyService.create(Notify.builder().user(user).message("Welcome to application").title("Titolo").priority(Notify.Priority.LOW).read(false).build()));
    } else {
      notifies.forEach(notify -> log.info("Notify found in DB: {}", notify));
      log.info("Notifies already exist in the database, doing nothing");
    }
  }
  
  private void initUsers() {
    List<User> users = userService.findAll().orElse(null);
    if (users == null || users.isEmpty()) {
      log.info("No User found in DB, adding some default Users");
      userService.save(User.builder().username("admin@test.com").password("admin-12345X").activated(true).build());
      userService.save(User.builder().username("user@test.com").password("user-12345X").activated(true).build());
      userService.save(User.builder().username("guest@test.com").password("guest-12345X").activated(true).build());
    } else {
      users.forEach(user -> log.info("User found in DB: {}", user));
      log.info("Users already exist in the database, doing nothing");
    }
  }
}
