package com.example.online_learning.dto.response;

import com.example.online_learning.constants.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoRes {
    private String username;
    private String email;
    private String name;
    private UserRole role;
}
