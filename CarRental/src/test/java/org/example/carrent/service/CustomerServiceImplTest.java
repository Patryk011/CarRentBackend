package org.example.carrent.service;
import org.example.carrent.dto.CustomerDTO;
import org.example.carrent.entity.Customer;
import org.example.carrent.exception.ResourceNotFoundException;
import org.example.carrent.mapper.CustomerMapper;
import org.example.carrent.repository.CustomerRepository;
import org.example.carrent.service.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Customer createTestCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhoneNumber("1234567890");
        customer.setBirthDate(new Date());
        customer.setLicenseNumber("D123456");
        customer.setAddress("123 Main St");
        customer.setRegistrationDate(System.currentTimeMillis());
        customer.setDiscountPercentage(10);
        customer.setKeycloakId(UUID.randomUUID());
        return customer;
    }

    @Test
    void testGetAllCustomers() {
        Customer customer = createTestCustomer();
        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals(CustomerMapper.toDto(customer).getEmail(), result.get(0).getEmail());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testAddCustomer() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setEmail("john.doe@example.com");

        Customer customer = createTestCustomer();
        when(customerMapper.toEntity(customerDTO)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO result = customerService.addCustomer(customerDTO);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(customerMapper, times(1)).toEntity(customerDTO);
    }

    @Test
    void testFindById() {
        Long id = 1L;
        Customer customer = createTestCustomer();
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.findByID(id);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void testFindByIdThrowsException() {
        Long id = 1L;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.findByID(id));
        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateCustomer() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setEmail("john.doe@example.com");

        Customer customer = createTestCustomer();
        when(customerRepository.findById(customerDTO.getId())).thenReturn(Optional.of(customer));
        when(customerMapper.toEntity(customerDTO)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO result = customerService.updateCustomer(customerDTO);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testFindByEmail() {
        String email = "john.doe@example.com";
        Customer customer = createTestCustomer();
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.findByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(customerRepository, times(1)).findByEmail(email);
    }

    @Test
    void testDeleteCustomer() {
        Long id = 1L;
        Customer customer = createTestCustomer();
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(id);

        verify(customerRepository, times(1)).deleteById(id);
    }

    @Test
    void testGetCustomerByToken() {
        UUID keycloakId = UUID.randomUUID();
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn(keycloakId.toString());

        Customer customer = createTestCustomer();
        customer.setKeycloakId(keycloakId);
        when(customerRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.getCustomerByToken(jwt);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(customerRepository, times(1)).findByKeycloakId(keycloakId);
    }
}
