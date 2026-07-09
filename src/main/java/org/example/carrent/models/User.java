package org.example.carrent.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "passwordHash")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private String address;

    public User copy() {
        return User.builder()
                .id(id)
                .login(login)
                .passwordHash(passwordHash)
                .role(role)
                .address(address)
                .build();
    }
}