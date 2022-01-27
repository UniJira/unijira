package it.unical.unijira.services.common;

import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.projects.Membership;
import it.unical.unijira.data.models.projects.MembershipKey;
import it.unical.unijira.data.models.projects.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {

    Optional<Project> findById(Long id);
    Optional<Project> save(Project project);
    Optional<Project> create(Project project);
    Optional<Project> update(Long id, Project project);
    void delete(Project project);
    List<Project> findAllByOwnerId(Long userId, int page, int size);
    List<Project> findAllByMemberId(Long userId, int page, int size);
    List<Membership> sendInvitations(Project project, List<User> users);
    Optional<Membership> createMembership(Project project, User user, Membership.Role role, Membership.Status status, Boolean owner);
    Optional<Membership> updateMembership(Long projectId, Long userId, Membership membership);
    Boolean deleteMembership(Long projectId, Long userId);
    Boolean verifyPermission(Long projectId, Long userId, Membership.Permission permission);
    boolean activate(MembershipKey key);

}
