package com.PayMyBuddy.PayMyBuddy.Controller.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import com.PayMyBuddy.PayMyBuddy.controller.RegisterController;
import com.PayMyBuddy.PayMyBuddy.model.User;
import com.PayMyBuddy.PayMyBuddy.service.UserService;

@SpringBootTest
public class RegisterControllerTest {

	@InjectMocks
	private RegisterController  registerController ;

	@Mock
	private UserService userService;

	@Mock
	private Model model;

	@Test
	void testInscriptionPage() {

		String viewName = registerController.inscriptionPage(new User(), model);


		assertEquals("register", viewName);
		verify(model).addAttribute("user", new User());
	}

	@Test
	void testInscription_PasswordMismatch() {

		User user = new User();
		user.setPassword("password123");


		String viewName = registerController.inscription("differentPassword", user, model);


		assertEquals("register", viewName);
		verify(model).addAttribute("errorPassword", "Passwords do not match.");
	}

	@Test
	void testInscription_Success() {

		User user = new User();
		user.setPassword("password123");

		when(userService.inscription(user))
		.thenReturn(new ResponseEntity<>("Successful registration", HttpStatus.CREATED));


		String viewName = registerController.inscription("password123", user, model);


		assertEquals("redirect:/login?success=Successful registration", viewName);
	}

	@Test
	void testInscription_Error() {

		User user = new User();
		user.setPassword("password123");

		when(userService.inscription(user))
		.thenReturn(new ResponseEntity<>("Registration error", HttpStatus.BAD_REQUEST));


		String viewName = registerController.inscription("password123", user, model);


		assertEquals("register", viewName);
		verify(model).addAttribute("error", "Registration error");
	}

}
