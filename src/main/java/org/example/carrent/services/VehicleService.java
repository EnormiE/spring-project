package org.example.carrent.services;

import lombok.RequiredArgsConstructor;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleService implements VehicleServiceInterface {

    private final VehicleValidator vehicleValidator;
    private final VehicleRepository vehicleRepository;

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicle.setRented(false);
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle updateVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> findById(String id) {
        return vehicleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleRented(String id) {
        return vehicleRepository.findById(id).map(Vehicle::isRented).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findByRentedFalse();
    }

    @Override
    public void removeVehicle(String id) throws IllegalStateException {
        if (isVehicleRented(id)) {
            throw new IllegalStateException("Nie można usunąć pojazdu, który jest wypożyczony");
        }
        vehicleRepository.deleteById(id);
    }
}