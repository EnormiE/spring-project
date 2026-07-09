package org.example.carrent.services;

import org.example.carrent.models.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalServiceInterface {

    Rental rentVehicle(String userId, String vehicleId) throws Exception;

    Rental returnVehicle(String userId) throws Exception;

    Optional<Rental> findActiveRentalByUserId(String userId);

    List<Rental> findAllRentals();

    List<Rental> findUserRentals(String userId);

    boolean userHasActiveRental(String userId);

    boolean vehicleHasActiveRental(String vehicleId);

    boolean hasActiveRentals(String userId); // Dla UserService (separacja domen)
}