package com.example.online_learning.controller;

import com.example.online_learning.constants.UserRole;
import com.example.online_learning.dto.request.LoginDtoReq;
import com.example.online_learning.dto.request.RegisterDtoReq;
import com.example.online_learning.dto.response.LoginDtoRes;
import com.example.online_learning.entity.User;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDtoReq request) {
        try {
            // 🔹 Check if user exists
            User user = userRepository.findByUserName(request.getUsername())
                    .orElseThrow(() ->
                            new BadCredentialsException("Invalid username or password"));

            // 🔹 Check if account is active
            if (user.getActive() == null || !user.getActive()) {
                throw new DisabledException("User account is inactive");
            }

            // 🔹 Authenticate user password
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(),
                                    request.getPassword()
                            )
                    );

            // 🔹 Get user details
            CustomUserDetail userDetails =
                    (CustomUserDetail) authentication.getPrincipal();

            // 🔹 Generate JWT token
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new LoginDtoRes(token));

        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));

        } catch (DisabledException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "User account is inactive"));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + ex.getMessage()));
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterDtoReq request) {
        User user = new User();
        user.setUserName(request.getUsername());
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.STUDENT);
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setActive(true);
        userRepository.save(user);
        return "Register success";
    }
}


