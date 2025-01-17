package org.example.carrent.Controllers;

import org.example.carrent.Utils.SecurityUtils;
import org.example.carrent.dto.CarDTO;
import org.example.carrent.dto.CustomerDTO;
import org.example.carrent.dto.RentalDTO;
import org.example.carrent.entity.Customer;
import org.example.carrent.service.CarService;
import org.example.carrent.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentalService;
    private final SecurityUtils securityUtils;

    @Autowired
    public RentalController(RentalService rentalService, SecurityUtils securityUtils) {
        this.rentalService = rentalService;
        this.securityUtils = securityUtils;
    }



    @GetMapping("/all")
    public List<RentalDTO> getAllRentals() {
        return rentalService.getAllRentals();
    }


    @GetMapping("/my")
    public ResponseEntity<List<RentalDTO>> getMyRentals(Authentication authentication) {
        CustomerDTO customerDTO = securityUtils.getAuthenticatedUser(authentication);
        List<RentalDTO> rentals = rentalService.getRentalsByCustomerId(customerDTO.getId());
        return ResponseEntity.ok(rentals);
    }



    @PatchMapping("/{id}/cancel")
    public RentalDTO cancelRental(@PathVariable Long id, Authentication authentication) {
        CustomerDTO customer = securityUtils.getAuthenticatedUser(authentication);


        RentalDTO rental = rentalService.findRentalById(id);
        if (!rental.getCustomerId().equals(customer.getId())) {

            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to cancel this rental.");
        }


        return rentalService.cancelRental(id);
    }

    @PostMapping
    public ResponseEntity<RentalDTO> createRental(@RequestBody RentalDTO rentalRequest, Authentication authentication) {
        CustomerDTO customerDTO = securityUtils.getAuthenticatedUser(authentication);
        rentalRequest.setCustomerId(customerDTO.getId());
        RentalDTO createdRental = rentalService.addRental(rentalRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRental);
    }

    @PostMapping("/add")
    public ResponseEntity<RentalDTO> addRental(@RequestBody RentalDTO rentalRequest) {

        RentalDTO createdRental = rentalService.addRental(rentalRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRental);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        RentalDTO rentalDTO = rentalService.findRentalById(id);
        return ResponseEntity.ok(rentalDTO);
    }
}
