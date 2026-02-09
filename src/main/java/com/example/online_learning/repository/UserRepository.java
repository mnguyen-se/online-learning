package com.example.online_learning.repository;

import com.example.online_learning.constants.UserRole;
import com.example.online_learning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
}
