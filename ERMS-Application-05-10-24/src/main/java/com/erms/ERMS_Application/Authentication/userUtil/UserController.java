package com.erms.ERMS_Application.Authentication.userUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserUtilService userUtilService;

    // Endpoint to generate a random password for a user
    @GetMapping("/generate-password")
    public ResponseEntity<String> generatePassword() {
        String randomPassword = userUtilService.generateRandomPassword();
        return ResponseEntity.ok(randomPassword);
    }

    // Endpoint to change the password for a given user
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestParam String email,
                                                 @RequestParam String oldPassword,
                                                 @RequestParam String newPassword) {
        boolean isChanged = userUtilService.changePassword(email, oldPassword, newPassword);
        if (isChanged) {
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Old password is incorrect.");
        }
    }

    // Endpoint to verify the old password before changing
    @PostMapping("/verify-password")
    public ResponseEntity<String> verifyPassword(@RequestParam String email,
                                                 @RequestParam String oldPassword) {
        boolean isValid = userUtilService.verifyOldPassword(email, oldPassword);
        if (isValid) {
            return ResponseEntity.ok("Old password is valid.");
        } else {
            return ResponseEntity.badRequest().body("Old password is invalid.");
        }
    }
}

