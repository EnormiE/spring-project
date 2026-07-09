package org.example.carrent.web;

import lombok.RequiredArgsConstructor;
import org.example.carrent.models.Rental;
import org.example.carrent.repositories.RentalRepository;
import org.example.carrent.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final RentalRepository rentalRepository;

    @PostMapping("/checkout/{rentalId}")
    public ResponseEntity<?> createCheckoutSession(@PathVariable String rentalId) {
        try {
            Rental rental = rentalRepository.findById(rentalId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono wypożyczenia o ID: " + rentalId));

            String paymentUrl = paymentService.createPaymentLink(rental);

            return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}