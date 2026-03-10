package com.example.online_learning.service;

import com.example.online_learning.dto.request.ChangePasswordDtoReq;
import com.example.online_learning.dto.request.CreateUserDtoReq;
import com.example.online_learning.dto.request.UpdateUserDtoReq;
import com.example.online_learning.dto.response.UserDtoRes;
import java.util.List;

public interface UserService {
    UserDtoRes findUserByUserName(String username);
    List<UserDtoRes> getAll();
    List<UserDtoRes> getAllTeachers();
    UserDtoRes createUser(CreateUserDtoReq request);
    UserDtoRes updateUser(Long userId, UpdateUserDtoReq request);
    void deleteUser(Long userId);
    void changePassword(Long userId, ChangePasswordDtoReq request);
}
