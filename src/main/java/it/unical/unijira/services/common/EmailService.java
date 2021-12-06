package it.unical.unijira.services.common;

public interface EmailService {
    boolean send(String to, String subject, String body, String... attachments);
}
