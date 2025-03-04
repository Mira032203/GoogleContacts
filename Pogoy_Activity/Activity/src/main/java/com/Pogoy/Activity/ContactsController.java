package com.Pogoy.Activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/contacts")
public class ContactsController {
    @Autowired
    private GoogleContactsService contactsService;

    @GetMapping("/view")
    public String getContactsPage(OAuth2AuthenticationToken authentication, Model model) {
        try {
            String response = contactsService.getContacts(authentication);

            // Parse the JSON response and convert it into a list of contacts
            List<Contact> contacts = GoogleContactsParser.parseContacts(response);

            model.addAttribute("contacts", contacts);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve contacts: " + e.getMessage());
        }
        return "contacts";
    }

    @PostMapping("/add")
    public String addContact(OAuth2AuthenticationToken authentication,
                             @RequestParam String name,
                             @RequestParam String email) {
        contactsService.addContact(authentication, name, email);
        return "redirect:/contacts/view"; // Refresh contacts page
    }

    @PostMapping("/update")
    public String updateContact(OAuth2AuthenticationToken authentication,
                                @RequestParam String resourceName,
                                @RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String etag) {  // ADD THIS
        contactsService.updateContact(authentication, resourceName, name, email, etag);
        return "redirect:/contacts/view";
    }

    @PostMapping("/delete")
    public String deleteContact(OAuth2AuthenticationToken authentication,
                                @RequestParam String resourceName) {
        contactsService.deleteContact(authentication, resourceName);
        return "redirect:/contacts/view"; // Refresh contacts page
    }
}
