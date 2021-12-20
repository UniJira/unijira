package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.models.TokenType;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.auth.AuthService;
import it.unical.unijira.services.common.EmailService;
import it.unical.unijira.services.common.UserService;
import it.unical.unijira.utils.Config;
import it.unical.unijira.utils.Locale;
import it.unical.unijira.utils.RegexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Locale locale;
    private final EmailService emailService;
    private final Config config;

    @Autowired
    public UserServiceImpl(AuthService authService, UserRepository userRepository, PasswordEncoder passwordEncoder, Config config, Locale locale, EmailService emailService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.locale = locale;
        this.emailService = emailService;
        this.config = config;
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
    public boolean activate(Long id) {

        return userRepository.findById(id)
                .stream()
                .peek(user -> user.setActivated(true))
                .peek(userRepository::saveAndFlush)
                .findFirst()
                .isPresent();

    }

}
