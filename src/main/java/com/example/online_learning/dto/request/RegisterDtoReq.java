package com.example.online_learning.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDtoReq {
    private String username;
    private String password;
    private String email;
    private String name;
    private String address;
    private LocalDate dateOfBirth;
}
