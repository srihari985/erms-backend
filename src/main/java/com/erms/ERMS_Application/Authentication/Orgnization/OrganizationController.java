package com.erms.ERMS_Application.Authentication.Orgnization;

import com.erms.ERMS_Application.Authentication.auth.AuthenticationResponse;
import com.erms.ERMS_Application.Authentication.auth.AuthenticationService;
import com.erms.ERMS_Application.Authentication.user.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth/organize")
public class OrganizationController {

    private  final AuthenticationService authenticationService;
    private final OrganizationService organizationService;

    public OrganizationController(AuthenticationService authenticationService, OrganizationService organizationService) {
        this.authenticationService = authenticationService;
        this.organizationService = organizationService;
    }



    @PostMapping("/register/organization")
//    public ResponseEntity<Organization> registerOrganization(@RequestParam String name , @RequestParam String email , @RequestParam String  address, @RequestParam Role role) {
//        Organization organization = organizationService.registerOrganization(name, email,address, role);
//        return ResponseEntity.ok(organization);
//    }
    public ResponseEntity<String> registerOrganization(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String address,
            @RequestParam Role role) {
        // Call the service method to register the organization
        organizationService.registerOrganization(name, email, address, role);

        // Return a response indicating successful registration
        return ResponseEntity.ok("Organization registered successfully. An email with the generated password has been sent.");
    }

    @PostMapping("/register/admin/{organizationId}")
    public ResponseEntity<AuthenticationResponse> registerAdmin(
            @PathVariable String organizationId,
            @RequestParam String firstname,@RequestParam String lastname, @RequestParam String email,
            @RequestParam Role role) {
        try {
            AuthenticationResponse response = authenticationService.registerAdmin(organizationId,firstname,lastname,email,role);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthenticationResponse(e.getMessage()));
        }
    }




    @PostMapping("/authenticate")
    public ResponseEntity<OrganizationResponse> authenticateOrganization(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam Role role) {
        try {
            // Call the authentication service
            OrganizationResponse response = organizationService.authenticate(email, password, role);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Return a bad request with the error message
            return ResponseEntity.badRequest().body(new OrganizationResponse(e.getMessage(), null,null,null,null));
        }
    }



}
