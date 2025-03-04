package com.Pogoy.Activity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

public class GoogleContactsParser {
    public static List<Contact> parseContacts(String jsonResponse) {
        List<Contact> contacts = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode connections = root.path("connections");

            for (JsonNode connection : connections) {
                String resourceName = connection.path("resourceName").asText();
                String etag = connection.path("etag").asText(); // Extract etag
                String name = connection.path("names").get(0).path("givenName").asText();
                String email = connection.path("emailAddresses").get(0).path("value").asText();

                contacts.add(new Contact(name, email, resourceName, etag)); // Include etag
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }
}
