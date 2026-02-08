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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

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

    /**
     * 1️⃣ API đăng nhập
     * Trả về JWT token nếu đăng nhập thành công
     */
    @Operation(
            summary = "Đăng nhập hệ thống",
            description = """
                    Người dùng đăng nhập bằng username và password.
                    - Nếu thành công: trả về JWT token
                    - Nếu sai thông tin: trả về 401
                    - Nếu tài khoản bị khóa: trả về 403
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Đăng nhập thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginDtoRes.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Sai username hoặc password"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Tài khoản bị vô hiệu hóa"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDtoReq request) {
        try {
            User user = userRepository.findByUserName(request.getUsername())
                    .orElseThrow(() ->
                            new BadCredentialsException("Invalid username or password"));

            if (user.getActive() == null || !user.getActive()) {
                throw new DisabledException("User account is inactive");
            }

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(),
                                    request.getPassword()
                            )
                    );

            CustomUserDetail userDetails =
                    (CustomUserDetail) authentication.getPrincipal();

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

    /**
     * 2️⃣ API đăng ký tài khoản
     * Mặc định role STUDENT
     */
    @Operation(
            summary = "Đăng ký tài khoản mới",
            description = """
                    Tạo tài khoản người dùng mới với role STUDENT.
                    - Username phải là duy nhất
                    - Password sẽ được mã hóa trước khi lưu
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Đăng ký thành công"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu không hợp lệ"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống"
            )
    })
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
