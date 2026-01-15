package com.example.online_learning.service;

import com.example.online_learning.dto.request.createUserDtoReq;
import com.example.online_learning.dto.request.updateUserDtoReq;
import com.example.online_learning.dto.response.UserDtoRes;
import java.util.List;

public interface UserService {
    UserDtoRes findUserByUserName(String username);
    List<UserDtoRes> getAll();
    UserDtoRes createUser(createUserDtoReq request);
    UserDtoRes updateUser(Long userId, updateUserDtoReq request);
    void deleteUser(Long userId);
}
