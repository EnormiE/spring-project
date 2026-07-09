package org.example.carrent.services;

import org.example.carrent.models.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleServiceInterface {

    List<Vehicle> findAllVehicles();

    List<Vehicle> findAvailableVehicles();

    Optional<Vehicle> findById(String id);

    Vehicle addVehicle(Vehicle vehicle);

    Vehicle updateVehicle(Vehicle vehicle);

    void removeVehicle(String vehicleId) throws Exception;

    boolean isVehicleRented(String vehicleId);
}