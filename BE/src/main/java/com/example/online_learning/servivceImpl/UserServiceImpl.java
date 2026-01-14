package com.example.online_learning.servivceImpl;

import com.example.online_learning.dto.response.UserDtoRes;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.UserMapper;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
