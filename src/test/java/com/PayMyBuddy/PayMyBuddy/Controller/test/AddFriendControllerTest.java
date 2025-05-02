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
import com.PayMyBuddy.PayMyBuddy.controller.AddFriendController;
import com.PayMyBuddy.PayMyBuddy.service.ConnectionService;

@SpringBootTest
public class AddFriendControllerTest {

	@InjectMocks
	private AddFriendController addFriendController;

	@Mock
	private ConnectionService connectionService;

	@Mock
	private Model model;


	@Test
	void testAddFriendPage() {

		String viewName = addFriendController.addFriendPage();

		assertEquals("add-friend", viewName);
	}

	@Test
	void testAddFriend_Success() {

		String friendEmail = "friend@example.com";
		when(connectionService.createConnection(friendEmail))
		.thenReturn(new ResponseEntity<>("Friend successfully added", HttpStatus.OK));


		String viewName = addFriendController.addFriend(friendEmail, model);


		assertEquals("add-friend", viewName);
		verify(model).addAttribute("success", "Friend successfully added");
	}

	@Test
	void testAddFriend_Error() {

		String friendEmail = "friend@example.com";
		when(connectionService.createConnection(friendEmail))
		.thenReturn(new ResponseEntity<>("Add friend error", HttpStatus.NOT_FOUND));


		String viewName = addFriendController.addFriend(friendEmail, model);


		assertEquals("add-friend", viewName);
		verify(model).addAttribute("error", "Add friend error");
	}

}
