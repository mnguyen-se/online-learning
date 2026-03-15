package com.example.online_learning.controller;

import com.example.online_learning.constants.UserRole;
import com.example.online_learning.dto.request.ChangePasswordDtoReq;
import com.example.online_learning.dto.request.ForgotPasswordDtoReq;
import com.example.online_learning.dto.request.LoginDtoReq;
import com.example.online_learning.dto.request.RegisterDtoReq;
import com.example.online_learning.dto.request.ResetPasswordDtoReq;
import com.example.online_learning.dto.response.LoginDtoRes;
import com.example.online_learning.entity.PasswordResetToken;
import com.example.online_learning.entity.User;
import com.example.online_learning.repository.PasswordResetTokenRepository;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.security.CustomUserDetail;
import com.example.online_learning.security.JwtUtil;
import com.example.online_learning.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
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
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService,
                          PasswordResetTokenRepository passwordResetTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
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

    @Operation(
            summary = "Đổi mật khẩu",
            description = "Người dùng đổi mật khẩu của chính mình"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Đổi mật khẩu thành công"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Mật khẩu cũ sai hoặc mật khẩu mới không khớp"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập"
            )
    })
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @jakarta.validation.Valid @RequestBody ChangePasswordDtoReq request
    ) {
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "New password is required"));
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Confirm password does not match"));
        }

        User user = userRepository.findById(userDetail.getUser().getUserId())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Old password is incorrect"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @Operation(
            summary = "Quên mật khẩu",
            description = "Gửi mã khôi phục qua email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đã gửi mã nếu email tồn tại"),
            @ApiResponse(responseCode = "400", description = "Email chưa được đăng ký trong hệ thống")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@jakarta.validation.Valid @RequestBody ForgotPasswordDtoReq request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email chưa được đăng ký"));
        }

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        passwordResetTokenRepository.save(token);
        emailService.sendPasswordResetCode(user.getEmail(), user.getName(), code);

        return ResponseEntity.ok(Map.of("message", "If email exists, a reset code has been sent"));
    }

    @Operation(
            summary = "Đặt lại mật khẩu",
            description = "Nhập mã khôi phục và đặt mật khẩu mới"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đặt lại mật khẩu thành công"),
            @ApiResponse(responseCode = "400", description = "Mã không hợp lệ hoặc hết hạn")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@jakarta.validation.Valid @RequestBody ResetPasswordDtoReq request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Confirm password does not match"));
        }

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid code or email"));
        }

        PasswordResetToken token = passwordResetTokenRepository
                .findTopByUser_UserIdAndCodeAndUsedFalseOrderByIdDesc(user.getUserId(), request.getCode())
                .orElse(null);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid code or email"));
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Code expired"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        passwordResetTokenRepository.save(token);

        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }
}
