package com.PayMyBuddy.PayMyBuddy.Controller.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
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
		when(connectionService.createConnection(friendEmail)).thenReturn("Friend successfully added");

		String viewName = addFriendController.addFriend(friendEmail, model);

		assertEquals("add-friend", viewName);
		verify(model).addAttribute("success", "Friend successfully added");
	}

	 @Test
	    void testAddFriend_IllegalArgumentException() {
	        String friendEmail = "friend@example.com";
	        String errorMessage = "Invalid email format";
	        doThrow(new IllegalArgumentException(errorMessage)).when(connectionService).createConnection(friendEmail);

	        String viewName = addFriendController.addFriend(friendEmail, model);

	        assertEquals("add-friend", viewName);
	        verify(model).addAttribute("error", errorMessage);
	    }

	    @Test
	    void testAddFriend_IllegalStateException() {
	        String friendEmail = "friend@example.com";
	        String errorMessage = "Friend already added";
	        doThrow(new IllegalStateException(errorMessage)).when(connectionService).createConnection(friendEmail);

	        String viewName = addFriendController.addFriend(friendEmail, model);

	        assertEquals("add-friend", viewName);
	        verify(model).addAttribute("error", errorMessage);
	    }

	    @Test
	    void testAddFriend_GenericException() {
	        String friendEmail = "friend@example.com";
	        doThrow(new RuntimeException("Unexpected")).when(connectionService).createConnection(friendEmail);

	        String viewName = addFriendController.addFriend(friendEmail, model);

	        assertEquals("add-friend", viewName);
	        verify(model).addAttribute("error", "An unexpected error occurred.");
	    }

}
