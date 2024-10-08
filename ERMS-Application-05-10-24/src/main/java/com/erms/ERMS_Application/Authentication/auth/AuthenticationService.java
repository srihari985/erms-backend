package com.erms.ERMS_Application.Authentication.auth;

import com.erms.ERMS_Application.Authentication.Orgnization.Organization;
import com.erms.ERMS_Application.Authentication.Orgnization.OrganizationRepository;
import com.erms.ERMS_Application.Authentication.admin.Admin;
import com.erms.ERMS_Application.Authentication.admin.AdminRepository;
import com.erms.ERMS_Application.Authentication.config.JwtService;
import com.erms.ERMS_Application.Authentication.managers.ManagerRepository;
import com.erms.ERMS_Application.Authentication.managers.Managers;
import com.erms.ERMS_Application.Authentication.saleManager.SaleManager;
import com.erms.ERMS_Application.Authentication.saleManager.SaleManagerRepository;
import com.erms.ERMS_Application.Authentication.sales.Sales;
import com.erms.ERMS_Application.Authentication.sales.SalesRepository;
import com.erms.ERMS_Application.Authentication.security.CustomUserDetails;
import com.erms.ERMS_Application.Authentication.security.CustomUserDetailsService;
import com.erms.ERMS_Application.Authentication.technician.Technician;
import com.erms.ERMS_Application.Authentication.technician.TechnicianRepository;
import com.erms.ERMS_Application.Authentication.token.Token;
import com.erms.ERMS_Application.Authentication.token.TokenRepository;
import com.erms.ERMS_Application.Authentication.token.TokenType;
import com.erms.ERMS_Application.Authentication.user.Role;
import com.erms.ERMS_Application.Authentication.userUtil.UserUtilService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;



@Service
public class AuthenticationService {

	private final AdminRepository adminRepository;
    private final ManagerRepository managerRepository;
    private final SaleManagerRepository saleManagerRepository;
    private final TechnicianRepository technicianRepository;
    private final SalesRepository salesRepository;
    private final TokenRepository tokenRepository;
    private final OrganizationRepository organizationRepository; // Add this
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    @Autowired
    private UserUtilService userUtilService;

    public AuthenticationService(AdminRepository adminRepository,
                                 ManagerRepository managerRepository,
                                 SaleManagerRepository saleManagerRepository,
                                 TechnicianRepository technicianRepository,
                                 SalesRepository salesRepository,
                                 TokenRepository tokenRepository,
                                 OrganizationRepository organizationRepository, // Add this
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService) {
        this.adminRepository = adminRepository;
        this.managerRepository = managerRepository;
        this.saleManagerRepository = saleManagerRepository;
        this.technicianRepository = technicianRepository;
        this.salesRepository = salesRepository;
        this.tokenRepository = tokenRepository;
        this.organizationRepository = organizationRepository; // Add this
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
    }

    // Register Method


    public AuthenticationResponse registerAdmin(String organizationId, String firstname, String lastname, String email, Role role,String password) {
        // Find the organization by organization ID
        Organization organization = organizationRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid organization ID"));

        // Check if the email is already in use
        if (adminRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Create new Admin and set the details
        Admin admin = new Admin();
        setUserDetails(admin,firstname,lastname,email,role,password);
        admin.setOrganization(organization); // Associate the admin with the organization

        // Save admin in the Admin table
        adminRepository.save(admin);

        // Generate JWT response
        return generateJwtResponse(admin);

    }



    public AuthenticationResponse registerManager(String adminId, String firstname, String lastname, String email, Role role, String password) {
        // Verify the admin based on the provided adminId
        Admin admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID"));

        // Check if the email is already in use
        if (managerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Create and populate the manager entity
        Managers manager = new Managers();
        setUserDetails(manager,firstname,lastname,email,role,password);
        manager.setAdmin(admin); // Associate manager with the respective admin

        // Save manager details in the manager table
        managerRepository.save(manager);

        // Generate and return JWT token for the manager
        return generateJwtResponse(manager);
    }


    public AuthenticationResponse registerSaleManager(String managersId, String firstname, String lastname, String email, Role role, String password) {
        // Verify the manager by managersId from the request
        Managers manager = managerRepository.findByManagersId(managersId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid manager ID"));

        // Check if the email is already in use
        if (saleManagerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Create and populate the sale manager entity
        SaleManager saleManager = new SaleManager();
        setUserDetails(saleManager,firstname,lastname,email ,role,password);
        saleManager.setManager(manager); // Associate the sale manager with the respective manager

        // Save sale manager details in the sale manager table
        saleManagerRepository.save(saleManager);

        // Generate and return JWT token for the sale manager
        return generateJwtResponse(saleManager);
    }




    public AuthenticationResponse registerTechnician(String salesManagerId, String firstname, String lastname, String email, Role role, String password) {
        // Verify the sale manager based on the provided saleManagerId
        SaleManager saleManager = saleManagerRepository.findBySalesManagerId(salesManagerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sale manager ID"));

        // Check if the email is already in use
        if (technicianRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Create and populate the technician entity
        Technician technician = new Technician();
        setUserDetails(technician,firstname,lastname,email,role,password );
        technician.setSaleManager(saleManager); // Associate the technician with the respective sale manager

        // Save technician details in the technician table
        technicianRepository.save(technician);

        // Generate and return JWT token for the technician
        return generateJwtResponse(technician);
    }



    public AuthenticationResponse registerSales(String salesManagerId, String firstname, String lastname, String email, Role role, String password) {
        // Verify the sale manager based on the provided saleManagerId
        SaleManager saleManager = saleManagerRepository.findBySalesManagerId(salesManagerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sale manager ID"));

        // Check if the email is already in use
        if (salesRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Create and populate the sales entity
        Sales sales = new Sales();
        setUserDetails(sales,firstname,lastname,email,role,password);
        sales.setSaleManager(saleManager); // Associate the sales with the respective sale manager

        // Save sales details in the sales table
        salesRepository.save(sales);

        // Generate and return JWT token for the sales
        return generateJwtResponse(sales);

    }


    private void setUserDetails(Object user, String firstname, String lastname, String email, Role role,String password) {
        if (user instanceof Admin) {
            Admin admin = (Admin) user;
            admin.setFirstname(firstname);
            admin.setLastname(lastname);
            admin.setEmail(email);
            admin.setRole(role);
            //            String pass=userUtilService.generateRandomPassword();

            admin.setPassword(passwordEncoder.encode(password));
            admin.setAdminId(userUtilService.generateUserRoleId(role));

        } else if (user instanceof Managers) {
            Managers manager = (Managers) user;
            manager.setFirstname(firstname);
            manager.setLastname(lastname);
            manager.setEmail(email);
            manager.setRole(role);
            //            String pass=userUtilService.generateRandomPassword();

//            // Use the provided password for testing, otherwise generate a random password
//            String encodedPassword = password != null ? passwordEncoder.encode(password) : passwordEncoder.encode(userUtilService.generateRandomPassword());

            manager.setPassword(passwordEncoder.encode(password));
            manager.setManagersId(userUtilService.generateUserRoleId(role));
        } else if (user instanceof SaleManager) {
            SaleManager saleManager = (SaleManager) user;
            saleManager.setFirstname(firstname);
            saleManager.setLastname(lastname);
            saleManager.setEmail(email);
            saleManager.setRole(role);
//              String pass=userUtilService.generateRandomPassword();

            saleManager.setPassword(passwordEncoder.encode(password));
            saleManager.setSalesManagerId(userUtilService.generateUserRoleId(role));
        } else if (user instanceof Technician) {
            Technician technician = (Technician) user;
            technician.setFirstname(firstname);
            technician.setLastname(lastname);
            technician.setEmail(email);
            technician.setRole(role);
//            String pass=userUtilService.generateRandomPassword();

//            // Use the provided password for testing, otherwise generate a random password
//            String encodedPassword = password != null ? passwordEncoder.encode(password) : passwordEncoder.encode(userUtilService.generateRandomPassword());

            technician.setPassword(passwordEncoder.encode(password));
            technician.setTechnicianId(userUtilService.generateUserRoleId(role));
        } else if (user instanceof Sales) {
            Sales sales = (Sales) user;
            sales.setFirstname(firstname);
            sales.setLastname(lastname);
            sales.setEmail(email);
//            String pass=userUtilService.generateRandomPassword();


            sales.setPassword(passwordEncoder.encode(password));
            sales.setRole(role);
            sales.setSalesId(userUtilService.generateUserRoleId(role));
        }
    }

    // Generate JWT Response

    private AuthenticationResponse generateJwtResponse(Object user) {
        UserDetails userDetails;
        if (user instanceof Organization){
            userDetails = new CustomUserDetails((Organization) user);
        }
        else if (user instanceof Admin) {
            userDetails = new CustomUserDetails((Admin) user);
        } else if (user instanceof Managers) {
            userDetails = new CustomUserDetails((Managers) user);
        } else if (user instanceof SaleManager) {
            userDetails = new CustomUserDetails((SaleManager) user);
        } else if (user instanceof Technician) {
            userDetails = new CustomUserDetails((Technician) user);
        } else if (user instanceof Sales) {
            userDetails = new CustomUserDetails((Sales) user);
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }

        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        saveUserToken(user, jwtToken);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }


    // Authenticate  Method

    public AuthenticationResponse authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    email,password
                )
        );

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        revokeAllUserTokens(userDetails);  // Update to accept UserDetails
        saveUserToken(userDetails, jwtToken);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }


    // Other helper methods like findUserByEmail, saveUserToken, revokeAllUserTokens,
    // refreshToken and generateUserRoleId etc.


    private Object findUserByEmail(String email) {
        Optional<Organization> organization = organizationRepository.findByEmail(email);
        if (organization.isPresent()) return organization.get();

        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) return admin.get();

        Optional<Managers> manager = managerRepository.findByEmail(email);
        if (manager.isPresent()) return manager.get();

        Optional<SaleManager> saleManager = saleManagerRepository.findByEmail(email);
        if (saleManager.isPresent()) return saleManager.get();

        Optional<Technician> technician = technicianRepository.findByEmail(email);
        if (technician.isPresent()) return technician.get();

        Optional<Sales> sales = salesRepository.findByEmail(email);
        if (sales.isPresent()) return sales.get();

        throw new IllegalArgumentException("User not found");
    }

    private void saveUserToken(Object user, String jwtToken) {
        Token token = new Token();
        token.setToken(jwtToken);
        token.setTokenType(TokenType.BEARER);
        token.setExpired(false);
        token.setRevoked(false);
        if (user instanceof CustomUserDetails) {
            token.setUser(((CustomUserDetails) user).getUser());
        }
        tokenRepository.save(token);
    }
    

    private void revokeAllUserTokens(UserDetails userDetails) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(getUserId(userDetails));
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Extract refresh token from request header
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Refresh token is missing or malformed");
        }

        final String refreshToken = authHeader.substring(7);
        String email = jwtService.extractUsername(refreshToken); // Assuming the refresh token contains the username

        if (email != null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            // Validate the refresh token
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String newAccessToken = jwtService.generateToken(userDetails);
                String newRefreshToken = jwtService.generateRefreshToken(userDetails);

                // Revoke all previous tokens and save the new tokens
                revokeAllUserTokens(userDetails);
                saveUserToken(userDetails, newAccessToken);

                // Send the new tokens back to the client in the response
                AuthenticationResponse authResponse = new AuthenticationResponse(newAccessToken, newRefreshToken);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            } else {
                throw new IllegalArgumentException("Invalid refresh token");
            }
        }
    }
    private Integer getUserId(UserDetails userDetails) {
        String email = userDetails.getUsername();
        // Use the repositories to get the user ID based on email
        if (adminRepository.findByEmail(email).isPresent()) {
            return Math.toIntExact(adminRepository.findByEmail(email).get().getId());
        } else if (managerRepository.findByEmail(email).isPresent()) {
            return Math.toIntExact(managerRepository.findByEmail(email).get().getId());
        } else if (saleManagerRepository.findByEmail(email).isPresent()) {
            return Math.toIntExact(saleManagerRepository.findByEmail(email).get().getId());
        } else if (technicianRepository.findByEmail(email).isPresent()) {
            return Math.toIntExact(technicianRepository.findByEmail(email).get().getId());
        } else if (salesRepository.findByEmail(email).isPresent()) {
            return Math.toIntExact(salesRepository.findByEmail(email).get().getId());
        }
        throw new IllegalArgumentException("User not found");
    }



}
