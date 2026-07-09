package org.example.carrent.web;

import org.example.carrent.models.Vehicle;
import org.example.carrent.services.VehicleServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleServiceInterface vehicleService;

    public VehicleController(VehicleServiceInterface vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> list(@RequestParam(name = "available", required = false, defaultValue = "false") boolean available) {
        return available ? vehicleService.findAvailableVehicles() : vehicleService.findAllVehicles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> get(@PathVariable String id) {
        return ResponseEntity.of(vehicleService.findById(id));
    }

    @PostMapping
    public Vehicle create(@RequestBody Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) throws Exception {
        vehicleService.removeVehicle(id);
        return ResponseEntity.noContent().build();
    }

    // ręczne GPS
    @PutMapping("/{id}/location")
    public ResponseEntity<Vehicle> updateLocation(
            @PathVariable String id,
            @RequestBody Map<String, Double> locationData) {

        Vehicle vehicle = vehicleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + id));

        if (locationData.containsKey("latitude")) {
            vehicle.setLatitude(locationData.get("latitude"));
        }
        if (locationData.containsKey("longitude")) {
            vehicle.setLongitude(locationData.get("longitude"));
        }

        return ResponseEntity.ok(vehicleService.updateVehicle(vehicle));
    }
}