package keycloak.spi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;

public class RegistrationEventListenerProvider implements EventListenerProvider {

    private static final Logger LOG = Logger.getLogger(RegistrationEventListenerProvider.class);
    private final KeycloakSession session;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RegistrationEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.REGISTER) {
            handleRegistration(event);
        }
        else if (event.getType() == EventType.LOGIN) {
            handleLogin(event);
        }
    }

    private void handleRegistration(Event event) {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, event.getUserId());

        if (user == null) {
            LOG.warn("User not found in handleRegistration");
            return;
        }

        HashMap<String, Object> customerData = collectUserData(user);
        customerData.put("registrationDate", Instant.now().toEpochMilli());

        String identityProvider = event.getDetails() != null
                ? event.getDetails().get("identity_provider")
                : null;
        if (identityProvider != null) {

            customerData.put("identityProvider", identityProvider);
        }

        postToSpringApp("/customer-sync/register", customerData);
    }


    private void handleLogin(Event event) {
        String identityProvider = event.getDetails() != null
                ? event.getDetails().get("identity_provider")
                : null;

        if (identityProvider != null) {
           return;
        }

        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, event.getUserId());

        if (user == null) {

            return;
        }


    }


    private HashMap<String, Object> collectUserData(UserModel user) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("keycloakId", user.getId());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("email", user.getEmail());
        data.put("phoneNumber", user.getFirstAttribute("phoneNumber"));
        data.put("birthDate", user.getFirstAttribute("birthDate"));
        return data;
    }


    private void postToSpringApp(String endpoint, HashMap<String, Object> body) {
        try {
            String requestBody = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://spring-backend:8081" + endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            System.out.println("Error");
        }
    }


    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        if (adminEvent.getResourceTypeAsString().equalsIgnoreCase("USER")) {
            if (adminEvent.getOperationType() == OperationType.UPDATE) {
                handleUserUpdate(adminEvent);
            } else if (adminEvent.getOperationType() == OperationType.DELETE) {
                handleUserDelete(adminEvent);
            }
        }
    }

    private void handleUserUpdate(AdminEvent adminEvent) {

        String resourcePath = adminEvent.getResourcePath();
        String userId = resourcePath.replace("users/", "");

        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, userId);
        if (user == null) {

            return;
        }

        HashMap<String, Object> updatedData = collectUserData(user);

        postToSpringApp("/customer-sync/update", updatedData);
    }

    private void handleUserDelete(AdminEvent adminEvent) {
        String resourcePath = adminEvent.getResourcePath();
        String userId = resourcePath.replace("users/", "");

        HashMap<String, Object> deletedData = new HashMap<>();
        deletedData.put("keycloakId", userId);

        postToSpringApp("/customer-sync/delete", deletedData);
    }

    @Override
    public void close() {
    }
}
