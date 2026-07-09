package org.example.carrent.services;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.example.carrent.models.Rental;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public String createPaymentLink(Rental rental) throws Exception {
        if (rental.getTotalCost() == null || rental.getTotalCost() <= 0) {
            throw new IllegalArgumentException("Wypożyczenie nie zostało jeszcze zakończone lub koszt wynosi 0.");
        }

        long amountInGrosze = (long) (rental.getTotalCost() * 100); // w groszach

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/swagger-ui/index.html")
                .setCancelUrl("http://localhost:8080/swagger-ui/index.html")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("pln")
                                                .setUnitAmount(amountInGrosze)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Opłata za wypożyczenie: " + rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}