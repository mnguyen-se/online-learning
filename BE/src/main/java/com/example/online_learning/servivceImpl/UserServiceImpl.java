package com.example.online_learning.servivceImpl;

import com.example.online_learning.dto.request.createUserDtoReq;
import com.example.online_learning.dto.response.UserDtoRes;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.UserMapper;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public UserDtoRes createUser(createUserDtoReq request) {
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
        user.setRole(request.getRole() != null ? request.getRole() : com.example.online_learning.constants.UserRole.STUDENT);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return userMapper.toDtoReq(savedUser);
    }

    @Override
    public UserDtoRes updateUser(Long userId, com.example.online_learning.dto.request.updateUserDtoReq request) {
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
        // Soft delete: chuyển active sang false thay vì xóa
        user.setActive(false);
        userRepository.save(user);
    }
}
