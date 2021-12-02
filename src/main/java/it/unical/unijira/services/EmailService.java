package it.unical.unijira.services;

public interface EmailService {
    boolean send(String to, String subject, String body, String... attachments);
}
