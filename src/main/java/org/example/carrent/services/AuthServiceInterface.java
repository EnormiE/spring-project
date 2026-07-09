package org.example.carrent.services;

import org.example.carrent.models.User;

import java.util.Optional;

public interface AuthServiceInterface {

    boolean register(String login, String rawPassword);

    Optional<User> login(String login, String rawPassword);
}