package org.example.carrent.service;

import jakarta.transaction.Transactional;
import org.example.carrent.dto.CustomerDTO;
import org.example.carrent.entity.Customer;
import org.example.carrent.exception.ResourceNotFoundException;
import org.example.carrent.mapper.CustomerMapper;
import org.example.carrent.repository.CustomerRepository;
import org.example.carrent.repository.PaymentRepository;
import org.example.carrent.repository.RentalRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PaymentRepository paymentRepository;
    private final RentalRepository  rentalRepository;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper,
            PaymentRepository paymentRepository, RentalRepository rentalRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
       return customerMapper.toDto(customerRepository.findAll());
    }
    @Override
    public CustomerDTO addCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        return customerMapper.toDto(customerRepository.save(customer));
    }

    @Override
    public CustomerDTO findByID(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer with id " + id + " not found"));
        return customerMapper.toDto(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        customerRepository.findById(customerDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Customer with id " + customerDTO.getId() + " not found"));
        Customer updatedCustomer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(updatedCustomer);
        return customerMapper.toDto(savedCustomer);
    }

    @Override
    public CustomerDTO findByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer with email " + email + " not found"));;
        return customerMapper.toDto(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer with id " + id + " not found"));
        customerRepository.deleteById(id);
    }


    @Override
    @Transactional
    public void deleteCustomerByKeycloakId(UUID keycloakId) {
        Optional<Customer> optionalCustomer = customerRepository.findByKeycloakId(keycloakId);
        if (optionalCustomer.isEmpty()) {
            return;
        }

        Customer customer = optionalCustomer.get();


        rentalRepository.deleteAllByCustomerId(customer.getId());
        paymentRepository.deleteAllByCustomerId(customer.getId());



        customerRepository.delete(customer);
    }


    @Override
    @Transactional
    public CustomerDTO updateCustomerByKeycloakId(CustomerDTO customerDTO) {
        if (customerDTO.getKeycloakId() == null) {
            throw new ResourceNotFoundException("Keycloak ID cannot be null for update.");
        }

        UUID keycloakUUID = customerDTO.getKeycloakId();
        Customer existingCustomer = customerRepository.findByKeycloakId(keycloakUUID)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with keycloakId " + customerDTO.getKeycloakId() + " not found"
                ));


        if (customerDTO.getFirstName() != null) {
            existingCustomer.setFirstName(customerDTO.getFirstName());
        }
        if (customerDTO.getLastName() != null) {
            existingCustomer.setLastName(customerDTO.getLastName());
        }
        if (customerDTO.getEmail() != null) {
            existingCustomer.setEmail(customerDTO.getEmail());
        }

        if (customerDTO.getPhoneNumber() != null) {
            existingCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
        }

        if (customerDTO.getBirthDate() != null) {
            existingCustomer.setBirthDate(customerDTO.getBirthDate());
        }



        Customer savedCustomer = customerRepository.save(existingCustomer);


        return customerMapper.toDto(savedCustomer);
    }




    @Override
    public CustomerDTO getCustomerByToken(Jwt principal) {
        String subAsString = principal.getClaimAsString("sub");
        if (subAsString == null) {
            throw new RuntimeException("No 'sub' claim found in the token!");
        }

        UUID subAsUuid;
        try {
            subAsUuid = UUID.fromString(subAsString);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Keycloak 'sub' is not a valid UUID: " + subAsString);
        }

        Customer customer = customerRepository.findByKeycloakId(subAsUuid)
                .orElseThrow(() -> new RuntimeException("No matching customer found for sub=" + subAsString));

        return customerMapper.toDto(customer);
    }


}
