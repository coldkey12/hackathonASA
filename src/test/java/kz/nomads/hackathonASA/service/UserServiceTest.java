package kz.nomads.hackathonASA.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kz.nomads.hackathonASA.model.User;
import kz.nomads.hackathonASA.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setUsername("testUser@mail.ru");
        mockUser.setPassword("password123");
    }

    @Test
    void testRegisterUser() {
        userService.registerUser(mockUser);
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser);
        when(request.getSession()).thenReturn(session);

        boolean result = userService.login(mockUser, request);

        assertTrue(result);
        verify(session).setAttribute("currentUser", mockUser);
    }

    @Test
    void testLogin_Failure() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        boolean result = userService.login(mockUser, request);

        assertFalse(result);
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void testIsUnique_True() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        boolean result = userService.isUnique("newUser");

        assertTrue(result);
    }

    @Test
    void testIsUnique_False() {
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser);

        boolean result = userService.isUnique("existingUser");

        assertFalse(result);
    }

    @Test
    void testGetAllUsers() {
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");
        anotherUser.setPassword("pass");

        List<User> mockUsers = Arrays.asList(mockUser, anotherUser);
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("testUser@mail.ru", result.get(0).getUsername());
        assertEquals("anotherUser", result.get(1).getUsername());
    }


    @Test
    void testGetUserByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser);

        User result = userService.getUserByUsername("testUser@mail.ru");

        assertNotNull(result);
        assertEquals("testUser@mail.ru", result.getUsername());
    }
}


