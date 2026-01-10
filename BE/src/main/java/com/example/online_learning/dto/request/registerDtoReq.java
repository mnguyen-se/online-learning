package com.example.online_learning.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class registerDtoReq {
    private String username;
    private String password;
    private String email;
    private String name;
}
