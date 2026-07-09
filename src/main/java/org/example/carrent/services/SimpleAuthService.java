package org.example.carrent.services;

import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class SimpleAuthService implements AuthServiceInterface {

    private final UserRepository userRepository;

    public SimpleAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String login, String password) {
        Optional<User> userOpt = userRepository.findByLogin(login);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public boolean register(String login, String password) {
        if (userRepository.findByLogin(login).isPresent()) {
            return false;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User newUser = User.builder()
//                .id(UUID.randomUUID().toString())
                .login(login)
                .passwordHash(hashedPassword)
                .role(Role.USER)
                .build();

        userRepository.save(newUser);
        return true;
    }
}