package org.example.carrent.services;

import org.example.carrent.models.Vehicle;
import org.example.carrent.models.VehicleCategoryConfig;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VehicleValidator {

    private final VehicleCategoryConfigService configService;

    public VehicleValidator(VehicleCategoryConfigService configService) {
        this.configService = configService;
    }

    public void validate(Vehicle vehicle) {
        if (vehicle == null) throw new IllegalArgumentException("Pojazd nie może być nullem.");

        validateBaseFields(vehicle);
        validateAttributes(vehicle.getAttributes(), configService.getByCategory(vehicle.getCategory()));
    }

    private void validateBaseFields(Vehicle vehicle) {
        requireNonBlank(vehicle.getCategory(), "Kategoria jest wymagana.");
        requireNonBlank(vehicle.getBrand(), "Marka jest wymagana.");
        requireNonBlank(vehicle.getModel(), "Model jest wymagany.");
        requireNonBlank(vehicle.getPlate(), "Numer rejestracyjny jest wymagany.");

        if (vehicle.getYear() <= 0) throw new IllegalArgumentException("Rok musi być dodatni.");
        if (vehicle.getPrice() < 0) throw new IllegalArgumentException("Cena nie może być ujemna.");
    }

    private void validateAttributes(Map<String, Object> actualAttributes, VehicleCategoryConfig config) {
        Map<String, String> expectedAttributes = config.getAttributes();
        for (String actualName : actualAttributes.keySet()) {
            if (!expectedAttributes.containsKey(actualName)) {
                throw new IllegalArgumentException("Nieobsługiwany atrybut dla kategorii "
                        + config.getCategory() + ": " + actualName);
            }
        }

        expectedAttributes.forEach((attrName, expectedType) -> {
            Object value = actualAttributes.get(attrName);
            if (value == null) {
                throw new IllegalArgumentException("Brak wymaganego atrybutu: " + attrName);
            }
            if (expectedType.equalsIgnoreCase("string") && value instanceof String str) {
                requireNonBlank(str, "Atrybut " + attrName + " nie może być pusty.");
            }

            boolean isValidType = switch (expectedType.toLowerCase()) {
                case "string" -> value instanceof String;
                case "number" -> value instanceof Number;
                case "boolean" -> value instanceof Boolean;
                case "integer" -> value instanceof Number n && n.doubleValue() % 1 == 0;
                default -> throw new IllegalArgumentException("Nieobsługiwany typ w configu: " + expectedType);
            };
            if (!isValidType) {
                throw new IllegalArgumentException("Atrybut " + attrName + " musi być typu " + expectedType + ".");
            }
        });
    }

    private void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}