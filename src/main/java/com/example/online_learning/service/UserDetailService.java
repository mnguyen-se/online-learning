package com.example.online_learning.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailService {
    public UserDetails loadUserByUsername(String username);
}
