package com.example.online_learning;

import com.cloudinary.Cloudinary;
import com.example.online_learning.security.JwtFilter;
import com.example.online_learning.security.JwtUtil;
import com.example.online_learning.service.UserDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
class OnlineLearningApplicationTests {
	@MockBean JwtUtil jwtUtil;
	@MockBean JwtFilter jwtFilter;
	@MockBean UserDetailService userDetailService;
	@MockBean
	AuthenticationManager authenticationManager;
	@MockBean
	PasswordEncoder passwordEncoder;
	@MockBean
	Cloudinary cloudinary;

	@Test
	void contextLoads() {
	}

}

