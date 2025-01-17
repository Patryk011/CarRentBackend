package org.example.carrent.service;
import org.example.carrent.dto.RentalDTO;
import org.example.carrent.entity.Car;
import org.example.carrent.entity.Customer;
import org.example.carrent.entity.Rental;
import org.example.carrent.enums.RentalStatus;
import org.example.carrent.exception.ResourceNotFoundException;
import org.example.carrent.mapper.RentalMapper;
import org.example.carrent.repository.RentalRepository;
import org.example.carrent.service.PaymentService;
import org.example.carrent.service.RentalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class RentalServiceImplTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindRentalByIdNotFound() {
        Long rentalId = 1L;
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            rentalService.findRentalById(rentalId);
        });
    }

    @Test
    void testCancelRentalNotFound() {
        Long rentalId = 2L;
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            rentalService.cancelRental(rentalId);
        });
    }

    @Test
    void testCancelRentalAlreadyFinished() {
        Long rentalId = 3L;
        Rental rental = new Rental();
        rental.setId(rentalId);
        rental.setStatus(RentalStatus.FINISHED);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        assertThrows(IllegalStateException.class, () -> {
            rentalService.cancelRental(rentalId);
        });
    }
}
