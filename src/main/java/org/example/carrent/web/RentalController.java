package org.example.carrent.web;

import lombok.RequiredArgsConstructor;
import org.example.carrent.dto.RentalRequest;
import org.example.carrent.models.Rental;
import org.example.carrent.models.User;
import org.example.carrent.services.RentalServiceInterface;
import org.example.carrent.services.UserServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalServiceInterface rentalService;
    private final UserServiceInterface userService;

    // tylko dla admina
    @GetMapping
    public List<Rental> list() {
        return rentalService.findAllRentals();
    }

    // tylko dla admina
    @GetMapping("/users/{userId}")
    public List<Rental> userRentals(@PathVariable String userId) {
        return rentalService.findUserRentals(userId);
    }

    // dla usera
    @GetMapping("/me")
    public ResponseEntity<List<Rental>> myRentals(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String login = userDetails.getUsername();

        User user = userService.findByLogin(login);
        if (user == null) {
            throw new RuntimeException("Nie znaleziono użytkownika: " + login);
        }

        return ResponseEntity.ok(rentalService.findUserRentals(user.getId()));
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rent(
            @RequestBody RentalRequest rentalRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws Exception {

        String login = userDetails.getUsername();

        User user = userService.findByLogin(login);
        if (user == null) {
            throw new RuntimeException("Nie znaleziono użytkownika: " + login);
        }

        Rental rental = rentalService.rentVehicle(user.getId(), rentalRequest.vehicleId());

        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(
            @AuthenticationPrincipal UserDetails userDetails
    ) throws Exception {

        String login = userDetails.getUsername();

        User user = userService.findByLogin(login);
        if (user == null) {
            throw new RuntimeException("Nie znaleziono użytkownika: " + login);
        }

        Rental rental = rentalService.returnVehicle(user.getId());

        return ResponseEntity.ok(rental);
    }
}