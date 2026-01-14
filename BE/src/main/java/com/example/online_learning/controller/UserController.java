package com.example.online_learning.controller;

import com.example.online_learning.dto.response.UserDtoRes;
import com.example.online_learning.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
