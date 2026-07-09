package org.example.carrent.services;

import org.example.carrent.models.User;

import java.util.List;
import java.util.Optional;

public interface UserServiceInterface {

    List<User> findAllUsers();

    Optional<User> findById(String id);

    User findByLogin(String login);

    void deleteUser(String id, String loggedUserId) throws Exception;

    User registerUser(String login, String password, String address);
}