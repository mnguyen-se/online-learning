package com.example.online_learning.controller;

import com.example.online_learning.constants.UserRole;
import com.example.online_learning.dto.request.loginDtoReq;
import com.example.online_learning.dto.request.registerDtoReq;
import com.example.online_learning.dto.response.loginDtoRes;
import com.example.online_learning.entity.User;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
    public loginDtoRes login(@RequestBody loginDtoReq request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(request.getUsername());
        return new loginDtoRes(token);
    }

    @PostMapping("/register")
    public String register(@RequestBody registerDtoReq request) {
        User user = new User();
        user.setUserName(request.getUsername());
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.STUDENT);
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        userRepository.save(user);
        return "Register success";
    }
}


