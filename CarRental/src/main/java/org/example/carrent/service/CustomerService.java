package org.example.carrent.service;

import org.example.carrent.dto.CustomerDTO;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDTO> getAllCustomers();

    CustomerDTO addCustomer(CustomerDTO customerDTO);

    CustomerDTO findByID(Long id);

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    CustomerDTO findByEmail(String email);

    void deleteCustomer(Long id);

    CustomerDTO getCustomerByToken(Jwt principal);

    void deleteCustomerByKeycloakId(UUID id);

    CustomerDTO updateCustomerByKeycloakId(CustomerDTO customerDTO);

}
