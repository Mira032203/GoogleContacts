package com.Pogoy.Activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleContactsService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleContactsService.class);

    private static final String CONTACTS_URL = "https://people.googleapis.com/v1/people/me/connections?personFields=names,emailAddresses";
    private static final String CREATE_CONTACT_URL = "https://people.googleapis.com/v1/people:createContact";
    private static final String UPDATE_CONTACT_URL = "https://people.googleapis.com/v1/{resourceName}:updateContact";
    private static final String DELETE_CONTACT_URL = "https://people.googleapis.com/v1/{resourceName}:deleteContact";
    @Autowired
    private OAuth2AuthorizedClientService clientService;

    private String getAccessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );
        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("OAuth2 access token is missing or expired.");
        }
        return client.getAccessToken().getTokenValue();
    }



    public String addContact(OAuth2AuthenticationToken authentication, String name, String email) {
        String token = getAccessToken(authentication);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> contact = new HashMap<>();
        contact.put("names", List.of(Map.of("givenName", name)));
        contact.put("emailAddresses", List.of(Map.of("value", email)));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(contact, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    CREATE_CONTACT_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create contact: " + e.getMessage(), e);
        }

    }
    public String updateContact(OAuth2AuthenticationToken authentication, String resourceName, String newName, String newEmail) {
        String token = getAccessToken(authentication);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.info("Updating contact: {}", resourceName);

        // Fetch the current contact details to get the `etag`
        String getUrl = "https://people.googleapis.com/v1/" + resourceName + "?personFields=names,emailAddresses,etag";
        HttpEntity<Void> getRequest = new HttpEntity<>(headers);

        ResponseEntity<Map> getResponse = restTemplate.exchange(getUrl, HttpMethod.GET, getRequest, Map.class);
        logger.info("Fetched Contact: {}", getResponse.getBody());

        String etag = (String) getResponse.getBody().get("etag");

        if (etag == null) {
            throw new RuntimeException("Failed to retrieve etag for contact update.");
        }

        // Prepare update payload with etag
        Map<String, Object> contactUpdate = new HashMap<>();
        contactUpdate.put("etag", etag);
        contactUpdate.put("names", List.of(Map.of("givenName", newName)));
        contactUpdate.put("emailAddresses", List.of(Map.of("value", newEmail)));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(contactUpdate, headers);

        try {
            // Use the predefined constant instead of inline URL
            String updateUrl = UPDATE_CONTACT_URL.replace("{resourceName}", resourceName) + "?updatePersonFields=names,emailAddresses";

            logger.info("PATCH Request URL: {}", updateUrl);
            logger.info("PATCH Request Body: {}", contactUpdate);

            ResponseEntity<String> response = restTemplate.exchange(updateUrl, HttpMethod.PATCH, request, String.class);

            logger.info("Update Response: {}", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            logger.error("Failed to update contact: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update contact: " + e.getMessage(), e);
        }
    }

    public void deleteContact(OAuth2AuthenticationToken authentication, String resourceName) {
        String token = getAccessToken(authentication);

        RestTemplate restTemplate = new RestTemplate();


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        String deleteUrl = DELETE_CONTACT_URL.replace("{resourceName}", resourceName);


        try {
            restTemplate.exchange(
                    deleteUrl,
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );
        } catch (RestClientException e) {
            logger.error("Error deleting contact '{}': {}", resourceName, e.getMessage(), e);
            throw new RuntimeException("Failed to delete contact: " + e.getMessage(), e);
        }
    }




    public String getContacts(OAuth2AuthenticationToken authentication) {
        String token = getAccessToken(authentication);
        RestTemplate restTemplate = new RestTemplate();


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                CONTACTS_URL,
                HttpMethod.GET,
                request,
                String.class
        );

        return response.getBody();
    }

}

