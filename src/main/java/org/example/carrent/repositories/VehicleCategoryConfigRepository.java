package org.example.carrent.repositories;

import org.example.carrent.models.VehicleCategoryConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleCategoryConfigRepository extends JpaRepository<VehicleCategoryConfig, String> {

    Optional<VehicleCategoryConfig> findByCategory(String category);
}