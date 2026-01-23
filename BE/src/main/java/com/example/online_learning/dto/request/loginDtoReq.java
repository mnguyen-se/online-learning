package com.example.online_learning.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDtoReq {
    private String username;
    private String password;
}
