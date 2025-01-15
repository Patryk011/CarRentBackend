package org.example.carrent.service;
import org.example.carrent.dto.PaymentDTO;
import org.example.carrent.entity.Customer;
import org.example.carrent.entity.Rental;
import org.example.carrent.payuConfiguration.PayUConfig;
import org.example.carrent.repository.PaymentRepository;
import org.example.carrent.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {

    @Mock
    private PayUConfig payUConfig;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(payUConfig.getPaymentUrl()).thenReturn("http://mock-payment-url.com");
        when(payUConfig.getAuthUrl()).thenReturn("http://mock-auth-url.com");
        when(payUConfig.getClientId()).thenReturn("mockClientId");
        when(payUConfig.getClientSecret()).thenReturn("mockClientSecret");

        RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {}))
                .thenReturn(Mono.error(new RuntimeException("Failed to retrieve access token")));
    }

    @Test
    void testCreatePaymentFailure() {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setRentalId(1L);
        paymentDTO.setAmount(new BigDecimal("100.00"));
        paymentDTO.setDescription("Test payment");

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setCustomer(new Customer());

        when(rentalRepository.findById(paymentDTO.getRentalId())).thenReturn(Optional.of(rental));

        assertThrows(RuntimeException.class, () -> {
            paymentService.createPayment(paymentDTO);
        });
    }
}
