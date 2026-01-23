package com.example.online_learning.controller;

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
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public UserDtoRes getUserInfo(String username){
        return userService.findUserByUserName(username);
    }

    @GetMapping("/getAll")
    public List<UserDtoRes> getAllUsers() {
        return userService.getAll();
    }

    @PostMapping("/create")
    public UserDtoRes createUser(@jakarta.validation.Valid @RequestBody CreateUserDtoReq request) {
        return userService.createUser(request);
    }

    @PutMapping("/{userId}")
    public UserDtoRes updateUser(@PathVariable Long userId, @RequestBody UpdateUserDtoReq request) {
        return userService.updateUser(userId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "User deleted successfully";
    }


}
