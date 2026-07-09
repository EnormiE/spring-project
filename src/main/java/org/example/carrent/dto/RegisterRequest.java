package org.example.carrent.dto;

public record RegisterRequest(
        String login,
        String password,
        String address
) {
}