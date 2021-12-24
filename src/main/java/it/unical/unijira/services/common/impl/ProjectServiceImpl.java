package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.dao.projects.MembershipRepository;
import it.unical.unijira.data.dao.projects.ProjectRepository;
import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.TokenType;
import it.unical.unijira.data.models.User;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public record ProjectServiceImpl(ProjectRepository projectRepository, NotifyService notifyService, AuthService authService,
                                 MembershipRepository membershipRepository, UserRepository userRepository,
                                 Locale locale, Config config) implements ProjectService {

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public Optional<Project> save(Project project) {
        return Optional.of(projectRepository.saveAndFlush(project));
    }

    @Override
    public Optional<Project> create(Project project) {

        var p = new Project();
        p.setName(project.getName());
        p.setKey(project.getKey());
        p.setOwner(project.getOwner());
        p.setMemberships(Collections.emptyList());

        p = projectRepository.saveAndFlush(p);

        this.createMembership(p, userRepository.getById(p.getOwner().getId()), Membership.Role.MEMBER, Membership.Status.ENABLED);

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

        return users.stream().map(user -> {

            var membership = this.createMembership(project, user, Membership.Role.MEMBER, Membership.Status.PENDING)
                    .stream()
                    .findFirst()
                    .orElseThrow(RuntimeException::new);


            membershipRepository.findById(new MembershipKey(user, project)).stream()
                    .findAny()
                    .map(member ->
                        authService.generateToken(TokenType.PROJECT_INVITE, Map.of(
                                "userId",    member.getKey().getUser().getId(),
                                "projectId", member.getKey().getProject().getId()
                        ))
                    ).stream().peek(token -> {

                        try {

                            notifyService.send(user,
                                    locale.get("NOTIFY_PROJECT_CONFIRM_SUBJECT"),
                                    locale.get("NOTIFY_PROJECT_CONFIRM_BODY",
                                            config.getBaseURL(), project.getId(), token
                                    ),
                                    URI.create("%s/projects/%d/invite?q=%s".formatted(config.getBaseURL(), project.getId(), token)).toURL(),
                                    Notify.Priority.HIGH,
                                    Notify.Mask.PROJECT_INVITE_RECEIVED
                            );

                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }

                    }).findFirst().orElseThrow(RuntimeException::new);

            return membership;

        }).toList();


    }

    @Override
    public Optional<Membership> createMembership(Project project, User user, Membership.Role role, Membership.Status status) {

        var m = new Membership();
        m.setKey(new MembershipKey(user, project));
        m.setRole(role);
        m.setStatus(status);

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
