package org.example.carrent.repositories;

import org.example.carrent.models.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {

    Optional<Rental> findByUser_IdAndReturnDateTimeIsNull(String userId);

    List<Rental> findByUser_Id(String userId);
}