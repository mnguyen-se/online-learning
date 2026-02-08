package com.example.online_learning.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.online_learning.dto.request.CreateUserDtoReq;
import com.example.online_learning.dto.request.UpdateUserDtoReq;
import com.example.online_learning.dto.response.UserDtoRes;
import com.example.online_learning.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Quản lý người dùng trong hệ thống")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 1️⃣ Lấy thông tin user theo username
     */
    @Operation(
            summary = "Lấy thông tin người dùng",
            description = "Lấy thông tin chi tiết của người dùng theo username"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lấy thông tin user thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDtoRes.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy user")
    })
    @GetMapping("/info")
    public UserDtoRes getUserInfo(
            @RequestParam String username
    ) {
        return userService.findUserByUserName(username);
    }

    /**
     * 2️⃣ Lấy danh sách toàn bộ user
     */
    @Operation(
            summary = "Lấy danh sách người dùng",
            description = "Lấy toàn bộ user trong hệ thống"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách user thành công")
    })
    @GetMapping("/getAll")
    public List<UserDtoRes> getAllUsers() {
        return userService.getAll();
    }

    /**
     * 3️⃣ Tạo user mới
     */
    @Operation(
            summary = "Tạo người dùng mới",
            description = "Tạo user mới (username, password, role, thông tin cá nhân)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo user thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping("/create")
    public UserDtoRes createUser(
            @jakarta.validation.Valid @RequestBody CreateUserDtoReq request
    ) {
        return userService.createUser(request);
    }

    /**
     * 4️⃣ Cập nhật thông tin user
     */
    @Operation(
            summary = "Cập nhật người dùng",
            description = "Cập nhật thông tin user theo userId"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật user thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy user")
    })
    @PutMapping("/{userId}")
    public UserDtoRes updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserDtoReq request
    ) {
        return userService.updateUser(userId, request);
    }

    /**
     * 5️⃣ Xóa user (ADMIN)
     */
    @Operation(
            summary = "Xóa người dùng",
            description = "Xóa user khỏi hệ thống (chỉ ADMIN)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa user thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy user")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "User deleted successfully";
    }
}
