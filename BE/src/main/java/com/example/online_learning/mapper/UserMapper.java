package com.example.online_learning.mapper;

import com.example.online_learning.dto.response.UserDtoRes;
import com.example.online_learning.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDtoRes toDtoReq(User user){
        UserDtoRes dto = new UserDtoRes();
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setName(user.getName());
        dto.setUsername(user.getUserName());
        return dto;
    }
}
