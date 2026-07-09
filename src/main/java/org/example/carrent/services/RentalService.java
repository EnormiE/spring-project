package org.example.carrent.services;

import lombok.RequiredArgsConstructor;
import org.example.carrent.models.Rental;
import org.example.carrent.models.User;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalService implements RentalServiceInterface {

    private final RentalRepository rentalRepository;
    private final VehicleServiceInterface vehicleService;

    private static final double LUBLIN_LAT = 51.2465;
    private static final double LUBLIN_LON = 22.5684;
    private static final double MAX_DISTANCE_KM = 0.5;

    @Override
    public Rental rentVehicle(String userId, String vehicleId) throws IllegalStateException, IllegalArgumentException {
        if (userHasActiveRental(userId)) {
            throw new IllegalStateException("Już masz wypożyczony pojazd, najpierw go zwróć, zanim wypożyczysz nowy");
        }

        Vehicle vehicle = vehicleService.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Brak pojazdu o takim ID"));

        if (vehicle.isRented()) {
            throw new IllegalStateException("Wybrany pojazd jest już przez kogoś wypożyczony");
        }

        vehicle.setRented(true);
        vehicleService.updateVehicle(vehicle);

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicle(vehicle)
                .user(User.builder().id(userId).build())
                .rentDateTime(LocalDateTime.now().toString())
                .returnDateTime(null)
                .build();

        return rentalRepository.save(rental);
    }

    @Override
    public Rental returnVehicle(String userId) throws IllegalStateException {
        Rental rental = findActiveRentalByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Brak wypożyczonego pojazdu"));

        Vehicle vehicle = rental.getVehicle();

        if (vehicle.getLatitude() == null || vehicle.getLongitude() == null) {
            throw new IllegalStateException("Brak sygnału GPS pojazdu. Administrator musi ręcznie podać lokalizację.");
        }

        double distance = calculateDistance(vehicle.getLatitude(), vehicle.getLongitude(), LUBLIN_LAT, LUBLIN_LON);
        if (distance > MAX_DISTANCE_KM) {
            throw new IllegalStateException(String.format("Pojazd znajduje się za daleko od bazy w Lublinie (%.2f km). Zwrot niemożliwy poza strefą (max 500m).", distance));
        }

        LocalDateTime rentDate = LocalDateTime.parse(rental.getRentDateTime());
        LocalDateTime returnDate = LocalDateTime.now();

        long hours = ChronoUnit.HOURS.between(rentDate, returnDate);
        long days = (long) Math.ceil(hours / 24.0);
        if (days == 0) days = 1;

        double totalCost = days * vehicle.getPrice();

        rental.setReturnDateTime(returnDate.toString());
        rental.setTotalCost(totalCost);

        vehicle.setRented(false);
        vehicleService.updateVehicle(vehicle);

        return rentalRepository.save(rental);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean vehicleHasActiveRental(String id) {
        return vehicleService.isVehicleRented(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findUserRentals(String id) {
        return rentalRepository.findByUser_Id(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveRentals(String userId) {
        return userHasActiveRental(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rental> findActiveRentalByUserId(String id) {
        return rentalRepository.findByUser_IdAndReturnDateTimeIsNull(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }
}