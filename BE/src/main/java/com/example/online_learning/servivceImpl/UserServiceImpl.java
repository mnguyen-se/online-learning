package com.example.online_learning.servivceImpl;

import com.example.online_learning.dto.response.UserDtoRes;
import com.example.online_learning.entity.User;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.mapper.UserMapper;
import com.example.online_learning.repository.UserRepository;
import com.example.online_learning.service.UserService;
import org.springframework.stereotype.Service;

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
}
