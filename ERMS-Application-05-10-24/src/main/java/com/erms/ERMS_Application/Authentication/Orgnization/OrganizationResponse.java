package com.erms.ERMS_Application.Authentication.Orgnization;

import com.erms.ERMS_Application.Authentication.user.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrganizationResponse {

    private String email;
    private  Role role;

//    @JsonProperty("message")
//    private String message;

    public OrganizationResponse(String email, Role role) {
        this.email = email;
        this.role = role;

    }

    // Default constructor
    public OrganizationResponse() {
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
