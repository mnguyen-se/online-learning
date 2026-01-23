package com.example.online_learning.dto.request;

import com.example.online_learning.constants.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDtoReq {
    private String name;
    private String address;
    private LocalDate dateOfBirth;
    private UserRole role;
    private Boolean active;
}
