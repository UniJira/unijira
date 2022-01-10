package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.models.Notify;
import it.unical.unijira.data.models.TokenType;
import it.unical.unijira.data.models.User;
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

        if(!User.Status.REQUIRE_PASSWORD.equals(user.getStatus()))
            user.setStatus(User.Status.REQUIRE_CONFIRM);


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
                .peek(user -> user.setStatus(User.Status.ACTIVE))
                .peek(userRepository::saveAndFlush)
                .findFirst()
                .isPresent();

    }

}
