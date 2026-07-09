package org.example.carrent.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.carrent.models.Vehicle;
import org.example.carrent.services.VehicleServiceInterface;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class GpsTrackerScheduler {

    private final VehicleServiceInterface vehicleService;
    private final Random random = new Random();

    @Scheduled(fixedRate = 10000) // 10s
    public void simulateGpsMovement() {
        List<Vehicle> rentedVehicles = vehicleService.findAllVehicles().stream()
                .filter(Vehicle::isRented)
                .toList();

        for (Vehicle vehicle : rentedVehicles) {
            if (vehicle.getLatitude() != null && vehicle.getLongitude() != null) {
                double latChange = (random.nextDouble() - 0.5) * 0.001;
                double lonChange = (random.nextDouble() - 0.5) * 0.001;

                vehicle.setLatitude(vehicle.getLatitude() + latChange);
                vehicle.setLongitude(vehicle.getLongitude() + lonChange);

                vehicleService.updateVehicle(vehicle);
            }
        }
    }
}