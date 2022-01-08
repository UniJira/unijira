package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.TokenType;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.auth.AuthService;
import it.unical.unijira.services.common.EmailService;
import it.unical.unijira.services.common.NotifyService;
import it.unical.unijira.services.common.UserService;
import it.unical.unijira.utils.Config;
import it.unical.unijira.utils.Locale;
import it.unical.unijira.utils.RegexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final NotifyService notifyService;
    private final Locale locale;
    private final Config config;

    @Autowired
    public UserServiceImpl(AuthService authService, UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, NotifyService notifyService, Config config, Locale locale) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.notifyService = notifyService;
        this.locale = locale;
        this.config = config;
    }


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAll(Integer page, Integer size) {
        return userRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) { return userRepository.findById(id); }

    @Override
    public Optional<User> save(User user) {


        final var username = user.getUsername();
        final var password = user.getPassword();


        if(!RegexUtils.isValidUsername(username))
            return Optional.empty();

        if(!RegexUtils.isValidPassword(password))
            return Optional.empty();


        user.setPassword(passwordEncoder.encode(password));
        user.setDisabled(false);
        user.setActivated(false);


        return Optional.of(userRepository.saveAndFlush(user)).map(owner -> {

            if(!emailService.send(username,
                    locale.get("MAIL_ACCOUNT_CONFIRM_SUBJECT"),
                    locale.get("MAIL_ACCOUNT_CONFIRM_BODY",
                            config.getBaseURL(),
                            authService.generateToken(TokenType.ACCOUNT_CONFIRM, Map.of("userId", owner.getId())))
            )) {
                throw new RuntimeException("Error sending email to %s".formatted(username));
            }

            return owner;

        });

    }

    @Override
    public Optional<User> resetPassword(Long id, String password) {

        return userRepository.findById(id).stream()
                .peek(user -> user.setPassword(passwordEncoder.encode(password)))
                .map(userRepository::saveAndFlush)
                .peek(user -> notifyService.send(user,
                        locale.get("NOTIFY_PASSWORD_RESET_SUCCESSFUL_SUBJECT"),
                        locale.get("NOTIFY_PASSWORD_RESET_SUCCESSFUL_BODY"), null, Notify.Priority.HIGH)
                ).findFirst();

    }

    @Override
    public boolean activate(Long id) {

        return userRepository.findById(id)
                .stream()
                .peek(user -> user.setActivated(true))
                .peek(userRepository::saveAndFlush)
                .findFirst()
                .isPresent();

    }

    @Override
    public List<User> getCollaborators(User user) {
        if (user != null) {
            return this.userRepository.findCollaborators(user);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Project> getProjects(User user) {
       return this.userRepository.findAllMyProjects(user);
    }

    @Override
    public Optional<User> update(Long id, User user) {
        return userRepository.findById(id)
                .stream()
                .peek(updatedUser -> {
                    updatedUser.setUsername(user.getUsername());
                    updatedUser.setAvatar(user.getAvatar());
                    updatedUser.setBirthDate(user.getBirthDate());
                    updatedUser.setFirstName(user.getFirstName());
                    updatedUser.setLastName(user.getLastName());
                    updatedUser.setRole(user.getRole());
                    updatedUser.setDescription(user.getDescription());
                    updatedUser.setLinkedin(user.getLinkedin());
                    updatedUser.setGithub(user.getGithub());
                    updatedUser.setPhoneNumber(user.getPhoneNumber());
                })
                .findFirst()
                .map(userRepository::saveAndFlush);
    }

}
