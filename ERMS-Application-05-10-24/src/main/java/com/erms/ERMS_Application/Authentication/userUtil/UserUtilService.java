package com.erms.ERMS_Application.Authentication.userUtil;

import com.erms.ERMS_Application.Authentication.Orgnization.OrganizationRepository;
import com.erms.ERMS_Application.Authentication.admin.Admin;
import com.erms.ERMS_Application.Authentication.admin.AdminRepository;
import com.erms.ERMS_Application.Authentication.auth.AuthenticationService;
import com.erms.ERMS_Application.Authentication.managers.Managers;
import com.erms.ERMS_Application.Authentication.managers.ManagerRepository;
import com.erms.ERMS_Application.Authentication.saleManager.SaleManager;
import com.erms.ERMS_Application.Authentication.saleManager.SaleManagerRepository;
import com.erms.ERMS_Application.Authentication.sales.Sales;
import com.erms.ERMS_Application.Authentication.sales.SalesRepository;
import com.erms.ERMS_Application.Authentication.technician.Technician;
import com.erms.ERMS_Application.Authentication.technician.TechnicianRepository;
import com.erms.ERMS_Application.Authentication.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserUtilService {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private SaleManagerRepository saleManagerRepository;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private SalesRepository salesRepository;


    private static final int PASSWORD_LENGTH = 8;

    // In-memory store for user passwords (replace with DB storage in real application)
    private Map<String, String> userPasswords = new HashMap<>();

    // Generate a random password
    public String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz1234567890";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Store the password for a given user email
    public void storePassword(String email, String password) {
        userPasswords.put(email, password);
    }

    // Verify if the provided old password matches the stored password for the user
    public boolean verifyOldPassword(String email, String oldPassword) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent() && adminOpt.get().getPassword().equals(oldPassword)) {
            return true;
        }

        Optional<Managers> managerOpt = managerRepository.findByEmail(email);
        if (managerOpt.isPresent() && managerOpt.get().getPassword().equals(oldPassword)) {
            return true;
        }

        Optional<SaleManager> saleManagerOpt = saleManagerRepository.findByEmail(email);
        if (saleManagerOpt.isPresent() && saleManagerOpt.get().getPassword().equals(oldPassword)) {
            return true;
        }

        Optional<Technician> technicianOpt = technicianRepository.findByEmail(email);
        if (technicianOpt.isPresent() && technicianOpt.get().getPassword().equals(oldPassword)) {
            return true;
        }

        Optional<Sales> salesOpt = salesRepository.findByEmail(email);
        if (salesOpt.isPresent() && salesOpt.get().getPassword().equals(oldPassword)) {
            return true;
        }

        return false;
    }

    // Update the password in the corresponding repository based on the user role
    public void updatePasswordInRepository(String email, String newPassword) {
        // Admin
        adminRepository.findByEmail(email).ifPresent(admin -> {
            admin.setPassword(newPassword);
            adminRepository.save(admin);
        });

        // Manager
        managerRepository.findByEmail(email).ifPresent(manager -> {
            manager.setPassword(newPassword);
            managerRepository.save(manager);
        });

        // Sale Manager
        saleManagerRepository.findByEmail(email).ifPresent(saleManager -> {
            saleManager.setPassword(newPassword);
            saleManagerRepository.save(saleManager);
        });

        // Technician
        technicianRepository.findByEmail(email).ifPresent(technician -> {
            technician.setPassword(newPassword);
            technicianRepository.save(technician);
        });

        // Sales
        salesRepository.findByEmail(email).ifPresent(sales -> {
            sales.setPassword(newPassword);
            salesRepository.save(sales);
        });
    }

    // Change password logic
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        if (verifyOldPassword(email, oldPassword)) {
            updatePasswordInRepository(email, newPassword); // Update password in the repository
            return true;
        }
        return false;
    }


    public String generateUserRoleId(Role role) {
        String prefix;
        List<String> lastRoleIdList;
        Pageable pageable = PageRequest.of(0, 1);  // Fetch only the latest record

        // Determine the prefix and get the last role ID from the respective repository
        switch (role) {
            case ORGANIZATION:
                prefix = "ORG";
                lastRoleIdList = organizationRepository.findUserRoleIdByRole(pageable);
                break;
            case ADMIN:
                prefix = "ADM";
                lastRoleIdList = adminRepository.findUserRoleIdByRole(pageable);
                break;
            case MANAGER:
                prefix = "MAN";
                lastRoleIdList = managerRepository.findUserRoleIdByRole(pageable);
                break;
            case SALE_MANAGER:
                prefix = "SMAN";
                lastRoleIdList = saleManagerRepository.findUserRoleIdByRole(pageable);
                break;
            case TECHNICIAN:
                prefix = "TECH";
                lastRoleIdList = technicianRepository.findUserRoleIdByRole(pageable);
                break;
            case SALES:
                prefix = "SALES";
                lastRoleIdList = salesRepository.findUserRoleIdByRole(pageable);
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
        // Determine the next ID number (default is 1)
        int nextId = 1;
        if (!lastRoleIdList.isEmpty()) {
            String lastRoleId = lastRoleIdList.get(0);
            String numericPart = lastRoleId.replace(prefix + "-", "");  // Extract the numeric part

            // Safely parse the numeric part
            try {
                nextId = Integer.parseInt(numericPart) + 1;  // Increment the number
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric part in the last role ID: " + lastRoleId);
            }
        }

        // Format and return the new userRoleId with leading zeros (e.g., ADM-001)
        return String.format("%s-%03d", prefix, nextId);
    }
}
