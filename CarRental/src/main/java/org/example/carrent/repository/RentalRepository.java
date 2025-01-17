package org.example.carrent.repository;

import org.example.carrent.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByCustomerId(Long customerId);


    @Modifying
    @Query("DELETE FROM Rental r WHERE r.customer.id = :customerId")
    void deleteAllByCustomerId(@Param("customerId") Long customerId);
}
