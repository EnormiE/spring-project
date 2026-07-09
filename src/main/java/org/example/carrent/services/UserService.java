package org.example.carrent.services;

import lombok.RequiredArgsConstructor;
import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.RentalRepository;
import org.example.carrent.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(String login, String password, String address) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalStateException("Użytkownik z loginem '" + login + "' już istnieje!");
        }

        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .login(login)
                .passwordHash(passwordEncoder.encode(password))
                .role(Role.USER)
                .address(address)
                .build();

        return userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

    @Override
    public void deleteUser(String toDeleteId, String whoRequestedId) throws IllegalStateException {
        User userToDelete = findById(toDeleteId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika do usunięcia"));

        if (userToDelete.getRole().equals(Role.ADMIN)) {
            long adminCount = findAllUsers().stream().filter(user -> user.getRole().equals(Role.ADMIN)).count();
            if (adminCount < 2) {
                throw new IllegalStateException("W systemie musi zostać CONAJMNIEJ 1 administrator");
            }
        }

        if (rentalRepository.findByUserIdAndReturnDateIsNull(toDeleteId).isPresent()) {
            throw new IllegalStateException("Nie można usunąć użytkownika, który ma wypożyczony pojazd");
        }

        User requester = findById(whoRequestedId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika żądającego"));

        if (toDeleteId.equals(whoRequestedId) || requester.getRole().equals(Role.ADMIN)) {
            userRepository.deleteById(toDeleteId);
        } else {
            throw new IllegalStateException("Brak uprawnień do usunięcia tego konta");
        }
    }
}