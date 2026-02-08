package com.example.online_learning.dto.response;

import com.example.online_learning.constants.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoRes {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String address;
    private LocalDate dateOfBirth;
    private UserRole role;
    private Boolean active;
}
