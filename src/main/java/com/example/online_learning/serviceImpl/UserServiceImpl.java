package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.UserRole;
import com.example.online_learning.dto.request.ChangePasswordDtoReq;
import com.example.online_learning.dto.request.CreateUserDtoReq;
import com.example.online_learning.dto.request.UpdateUserDtoReq;
import com.example.online_learning.dto.response.UserDtoRes;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.UserMapper;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.online_learning.dto.response.NewStudentStatsDtoRes;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDtoRes findUserByUserName(String username) {
        User user = userRepository.findByUserName(username).orElse(null);
        if(user == null) throw new NotFoundException("User not found");
        return userMapper.toDtoReq(user);
    }

    @Override
    public List<UserDtoRes> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDtoReq)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDtoRes> getAllTeachers() {
        return userRepository.findByRole(UserRole.TEACHER)
                .stream()
                .map(userMapper::toDtoReq)
                .collect(Collectors.toList());
    }

    @Override
    public UserDtoRes createUser(CreateUserDtoReq request) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUserName(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        User user = new User();
        user.setUserName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setRole(request.getRole());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return userMapper.toDtoReq(savedUser);
    }

    @Override
    public UserDtoRes updateUser(Long userId, UpdateUserDtoReq request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        
        User updatedUser = userRepository.save(user);
        return userMapper.toDtoReq(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDtoReq request) {
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("New password is required");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Confirm password does not match");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public List<NewStudentStatsDtoRes> getNewStudentStats(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1); // bao gồm luôn ngày hiện tại

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // findByRoleAndCreatedAtBetween
        List<User> students = userRepository.findByRoleAndCreatedAtBetween(UserRole.STUDENT, startDateTime, endDateTime);

        // Group by Date -> Count
        Map<LocalDate, Long> countByDate = students.stream()
                .filter(u -> u.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        u -> u.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        List<NewStudentStatsDtoRes> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 0; i < days; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            Long count = countByDate.getOrDefault(currentDate, 0L);
            result.add(NewStudentStatsDtoRes.builder()
                    .date(currentDate.format(formatter))
                    .count(count.intValue())
                    .build());
        }

        return result;
    }
}

