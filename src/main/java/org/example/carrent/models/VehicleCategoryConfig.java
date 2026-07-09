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
@Entity
@Table(name = "vehicle_category_config")
public class VehicleCategoryConfig {

    @Id
    @Column(nullable = false, unique = true)
    private String category;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, String> attributes = new HashMap<>();

    @Builder
    public VehicleCategoryConfig(String category, Map<String, String> attributes) {
        this.category = category;
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void addAttribute(String name, String type) {
        attributes.put(name, type);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public VehicleCategoryConfig copy() {
        return VehicleCategoryConfig.builder()
                .category(category)
                .attributes(new HashMap<>(attributes))
                .build();
    }
}