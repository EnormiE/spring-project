package org.example.carrent.models;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true)
    private String id;
    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;

    @Column(columnDefinition = "NUMERIC")
    private double price;

    @Column(nullable = false)
    private boolean rented = false;

    // GPS
    private Double latitude;
    private Double longitude;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, Object> attributes = new HashMap<>();

    @Builder
    public Vehicle(String id, String category, String brand, String model, int year, String plate, double price, boolean rented, Double latitude, Double longitude, Map<String, Object> attributes) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.rented = rented;
        this.latitude = latitude;
        this.longitude = longitude;
        this.attributes = (attributes == null) ? new HashMap<>() : new HashMap<>(attributes);
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void addAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
    }

    public Vehicle copy() {
        return Vehicle.builder()
                .id(id)
                .category(category)
                .brand(brand)
                .model(model)
                .year(year)
                .plate(plate)
                .price(price)
                .rented(rented)
                .latitude(latitude)
                .longitude(longitude)
                .attributes(new HashMap<>(attributes))
                .build();
    }
}