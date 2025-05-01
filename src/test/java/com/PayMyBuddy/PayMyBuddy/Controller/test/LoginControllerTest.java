package com.PayMyBuddy.PayMyBuddy.Controller.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;
import com.PayMyBuddy.PayMyBuddy.controller.LoginController;

@SpringBootTest
public class LoginControllerTest {

	@InjectMocks
	private LoginController loginController ;

	@Mock
	private Model model;

	@Test
	void testLoginPage_NoErrorNoLogout() {

		String viewName = loginController.loginPage(null, null, model);


		assertEquals("login", viewName);
		verify(model, never()).addAttribute("error", "Invalid email or password.");
		verify(model, never()).addAttribute("message", "You have been successfully logged out.");
	}

	@Test
	void testLoginPage_WithError() {

		String viewName = loginController.loginPage("someError", null, model);


		assertEquals("login", viewName);
		verify(model).addAttribute("error", "Invalid email or password.");
	}

	@Test
	void testLoginPage_WithLogout() {

		String viewName = loginController.loginPage(null, "someLogout", model);


		assertEquals("login", viewName);
		verify(model).addAttribute("message", "You have been successfully logged out.");
	}

}
