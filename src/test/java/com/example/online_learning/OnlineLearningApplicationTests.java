package com.example.online_learning;

import com.cloudinary.Cloudinary;
import com.example.online_learning.security.JwtUtil;
import com.example.online_learning.service.UserDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
class OnlineLearningApplicationTests {
	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private Cloudinary cloudinary;

	@MockBean
	private UserDetailService userDetailService;

	@Test
	void contextLoads() {
	}

}

