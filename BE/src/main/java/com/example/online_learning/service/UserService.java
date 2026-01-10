package com.example.online_learning.service;

import com.example.online_learning.dto.response.UserDtoRes;

public interface UserService {
    UserDtoRes findUserByUserName(String username);
}
