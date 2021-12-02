package it.unical.unijira.services.auth;

public interface AuthService {
    boolean authenticate(String username, String password);
    boolean isAuthenticated();
}
