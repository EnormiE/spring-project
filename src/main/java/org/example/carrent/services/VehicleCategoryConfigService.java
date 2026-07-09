package org.example.carrent.services;

import org.example.carrent.models.VehicleCategoryConfig;
import org.example.carrent.repositories.VehicleCategoryConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class VehicleCategoryConfigService {

    private final VehicleCategoryConfigRepository configRepository;

    public VehicleCategoryConfigService(VehicleCategoryConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public List<VehicleCategoryConfig> findAllCategories() {
        return configRepository.findAll();
    }

    public VehicleCategoryConfig getByCategory(String category) {
        return configRepository.findByCategory(category)
                .orElseThrow(() -> new IllegalArgumentException("Nieznana kategoria pojazdu: " + category));
    }

    public boolean categoryExists(String category) {
        return configRepository.findByCategory(category).isPresent();
    }
}