package com.Pogoy.Activity;

public class Contact {
    private String displayName;
    private String email;
    private String resourceName;
    private String etag; // Added etag field

    public Contact(String displayName, String email, String resourceName, String etag) {
        this.displayName = displayName;
        this.email = email;
        this.resourceName = resourceName;
        this.etag = etag;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}
