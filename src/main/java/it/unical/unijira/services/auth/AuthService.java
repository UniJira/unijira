package it.unical.unijira.services.auth;

public interface AuthService {
    String authenticate(String username, String password);
    void logout();
}
