package org.example.carrent.web;

import lombok.RequiredArgsConstructor;
import org.example.carrent.models.Rental;
import org.example.carrent.models.User;
import org.example.carrent.services.PaymentService;
import org.example.carrent.services.RentalServiceInterface;
import org.example.carrent.services.UserServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final RentalServiceInterface rentalService;
    private final UserServiceInterface userService;

    @PostMapping("/checkout")
    public ResponseEntity<?> createCheckoutSession(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByLogin(userDetails.getUsername());

            Rental rental = rentalService.findActiveRentalByUserId(user.getId())
                    .orElseThrow(() -> new IllegalStateException("Nie masz obecnie żadnego aktywnego wypożyczenia"));
            
            String paymentUrl = paymentService.createPaymentLink(rental);

            return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}