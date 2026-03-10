package com.example.online_learning.repository;

import com.example.online_learning.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findTopByUser_UserIdAndCodeAndUsedFalseOrderByIdDesc(Long userId, String code);
}

