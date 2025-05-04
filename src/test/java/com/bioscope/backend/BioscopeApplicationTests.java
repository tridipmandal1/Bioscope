package com.bioscope.backend;

import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.services.iface.BookingService;
import com.bioscope.backend.v01.services.iface.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BioscopeApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	UserService userService;
	@Autowired
	BookingService bookingService;

	@Test
	void someTest() {

	}
}
