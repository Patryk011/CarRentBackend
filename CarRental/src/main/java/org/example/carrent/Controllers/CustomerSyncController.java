package org.example.carrent.Controllers;

import org.example.carrent.dto.CustomerDTO;
import org.example.carrent.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/customer-sync")
public class CustomerSyncController {

    private final CustomerService customerService;


    public CustomerSyncController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerCustomer(@RequestBody CustomerDTO customerDTO) {
        customerService.addCustomer(customerDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateCustomer(@RequestBody CustomerDTO customerDTO) {
        if (customerDTO.getKeycloakId() == null) {
            return ResponseEntity.badRequest().build();
        }

        customerService.updateCustomerByKeycloakId(customerDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteCustomer(@RequestBody Map<String, Object> payload) {
        String keycloakIdStr = (String) payload.get("keycloakId");
        if (keycloakIdStr == null) {

            return ResponseEntity.badRequest().build();
        }



        UUID keycloakId = UUID.fromString(keycloakIdStr);

        System.out.println("Keycloak ID " + keycloakId);
        System.out.println("Keycloak id string " + keycloakIdStr);


        customerService.deleteCustomerByKeycloakId(keycloakId);
        return ResponseEntity.ok().build();
    }


}
