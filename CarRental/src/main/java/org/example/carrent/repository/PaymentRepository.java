package org.example.carrent.repository;


import org.example.carrent.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Modifying
    @Query("DELETE FROM Payment r WHERE r.customer.id = :customerId")
    void deleteAllByCustomerId(@Param("customerId") Long customerId);
}
