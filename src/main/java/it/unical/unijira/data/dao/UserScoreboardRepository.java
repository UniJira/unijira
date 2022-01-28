package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.UserScoreboard;
import it.unical.unijira.data.models.projects.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserScoreboardRepository extends JpaRepository<UserScoreboard, Long> {

    @Query(value="select avg (us.score) " +
                    "FROM UserScoreboard us " +
                    "where us.user = :user " +
                    "and us.sprint.backlog.project = :project")
    Float findAverageByUser(User user, Project project);

    @Query(value="select max (us.score) " +
            "FROM UserScoreboard us " +
            "where us.user = :user " +
            "and us.sprint.backlog.project = :project")
    Float findMaxByUser(User user, Project project);

    @Modifying
    @Query(value="DELETE FROM UserScoreboard usb where usb.sprint = :sprint")
    void deleteAllBySprint(Sprint sprint);
}
