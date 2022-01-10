package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogRepository;
import it.unical.unijira.data.dao.RoadmapRepository;
import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.dao.projects.MembershipRepository;
import it.unical.unijira.data.dao.projects.ProjectRepository;
import it.unical.unijira.data.models.*;
import it.unical.unijira.data.models.projects.Membership;
import it.unical.unijira.data.models.projects.MembershipKey;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.auth.AuthService;
import it.unical.unijira.services.common.NotifyService;
import it.unical.unijira.services.common.ProjectService;
import it.unical.unijira.utils.Config;
import it.unical.unijira.utils.Locale;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

@Service
public record ProjectServiceImpl(ProjectRepository projectRepository, NotifyService notifyService, AuthService authService,
                                 MembershipRepository membershipRepository, UserRepository userRepository,
                                 ProductBacklogRepository backlogRepository, RoadmapRepository roadmapRepository,
                                 Locale locale, Config config) implements ProjectService {

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public Optional<Project> save(Project project) {
        Project created = projectRepository.saveAndFlush(project);

        ProductBacklog backlog = new ProductBacklog();
        backlog.setProject(created);
        backlogRepository.saveAndFlush(backlog);

        Roadmap roadmap = new Roadmap();
        roadmap.setBacklog(backlog);

        roadmapRepository.saveAndFlush(roadmap);

        return Optional.of(project);
    }

    @Override
    public Optional<Project> create(Project project) {

        var p = new Project();
        p.setName(project.getName());
        p.setKey(project.getKey());
        p.setOwner(project.getOwner());
        p.setMemberships(Collections.emptyList());

        p = projectRepository.saveAndFlush(p);

        this.createMembership(p, userRepository.getById(p.getOwner().getId()), Membership.Role.MEMBER, Membership.Status.ENABLED, true);

        return Optional.of(p);

    }

    @Override
    public Optional<Project> update(Long id, Project project) {

        return projectRepository.findById(id)
                .stream()
                .peek(p -> {
                    p.setName(project.getName());
                    p.setKey(project.getKey());
                    p.setOwner(project.getOwner());
                    p.setIcon(project.getIcon());
                    p.setMemberships(project.getMemberships());
                })
                .findFirst()
                .map(projectRepository::saveAndFlush);
    }

    @Override
    public void delete(Project project) {
        projectRepository.delete(project);
    }

    @Override
    public List<Project> findAllByOwnerId(Long userId, int page, int size) {
        return projectRepository.findByOwnerId(userId, PageRequest.of(page, size));
    }

    @Override
    public List<Project> findAllByMemberId(Long userId, int page, int size) {
        return projectRepository.findByMembershipsKeyUserId(userId, PageRequest.of(page, size));
    }

    @Override
    public List<Membership> sendInvitations(Project project, List<User> users) {

        List<Membership> memberships = new ArrayList<>();

        users.forEach(user -> {

            memberships.add(this.createMembership(project, user, Membership.Role.MEMBER, Membership.Status.PENDING, false)
                    .stream()
                    .findFirst()
                    .orElseThrow(RuntimeException::new)
            );


            var member = membershipRepository.findById(
                    new MembershipKey(user, project))
                    .stream()
                    .findAny()
                    .orElseThrow(RuntimeException::new);

            var token = authService.generateToken(TokenType.PROJECT_INVITE,
                    Map.of("userId",    member.getKey().getUser().getId(),
                           "projectId", member.getKey().getProject().getId(),
                           "reset",     User.Status.REQUIRE_PASSWORD.equals(user.getStatus())));


            URL url = null;

            try {

                url = URI.create("%s/projects/%d/invite?q=%s&k=%d".formatted(config.getBaseURL(), project.getId(),
                        token, User.Status.REQUIRE_PASSWORD.equals(user.getStatus()) ? 1 : 0)).toURL();

            } catch (MalformedURLException ignored) {}

            notifyService.send(user,
                    locale.get("NOTIFY_PROJECT_CONFIRM_SUBJECT"),
                    locale.get("NOTIFY_PROJECT_CONFIRM_BODY",
                            config.getBaseURL(),
                            project.getId(),
                            token,
                            User.Status.REQUIRE_PASSWORD.equals(user.getStatus()) ? 1 : 0),
                    url,
                    Notify.Priority.HIGH
            );

        });

        return memberships;

    }

    @Override
    public Optional<Membership> updateMembership(Long projectId, Long userId, Membership membership) {

        return membershipRepository.findById(new MembershipKey(userRepository.getById(userId), projectRepository.getById(projectId)))
                .stream()
                .peek(m -> {
                    m.setRole(membership.getRole());
                    m.setPermissions(membership.getPermissions());
                })
                .findFirst()
                .map(membershipRepository::saveAndFlush);

    }

    @Override
    public Optional<Membership> createMembership(Project project, User user, Membership.Role role, Membership.Status status, Boolean owner) {

        var m = new Membership();
        m.setKey(new MembershipKey(user, project));
        m.setRole(role);
        m.setStatus(status);

        if(owner) {
            m.setPermissions(new HashSet<>(Arrays.asList(Membership.Permission.ADMIN, Membership.Permission.DETAILS,
                    Membership.Permission.INVITATIONS, Membership.Permission.ROLES)));
        } else {
            m.setPermissions(Collections.emptySet());
        }

        return Optional.of(membershipRepository.saveAndFlush(m));

    }

    @Override
    public boolean activate(MembershipKey key) {

        return membershipRepository.findById(key)
                .stream()
                .peek(membership -> membership.setStatus(Membership.Status.ENABLED))
                .peek(membershipRepository::saveAndFlush)
                .findFirst()
                .isPresent();

    }

}
