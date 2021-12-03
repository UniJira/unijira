package it.unical.unijira.services.impl;

import it.unical.unijira.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
public record EmailServiceImpl(JavaMailSender mailSender) implements EmailService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);


    @Autowired
    public EmailServiceImpl {}

    @Override
    public boolean send(String to, String subject, String body, String... attachments) {

        try {

            LOGGER.info("Sending email to {} with subject '{}'", to, subject);

            final var mime = mailSender.createMimeMessage();
            final var helper = new MimeMessageHelper(mime, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            for (final var attachment : attachments)
                helper.addAttachment(attachment, new FileSystemResource(new File(attachment)));

            mailSender.send(mime);


        } catch (Exception e) {
            LOGGER.error("Error sending email to {}: {}", to, e);
            return false;
        }

        return true;

    }

}
