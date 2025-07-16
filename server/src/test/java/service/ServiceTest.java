
package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
    private UserService userService;

    //setup
    @BeforeEach
    public void setup() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }


    //test registration
    @Test
    public void testRegisterPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("testUser", "password", "email@example.com");
        RegisterResult result = userService.register(request);
        assertNotNull(result.authToken());
        assertEquals("testUser", result.username());
    }

    @Test
    public void testRegisterNegative() {
        RegisterRequest request = new RegisterRequest("testUser", "password", "email@example.com");
        assertDoesNotThrow(() -> userService.register(request));
        assertThrows(DataAccessException.class, () -> userService.register(request));
    }


    //test login
    @Test
    public void testLoginPositive() throws DataAccessException {
        RegisterRequest regRequest = new RegisterRequest("user1", "pass1", "u1@example.com");
        userService.register(regRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "pass1");
        LoginResult result = userService.login(loginRequest);
        assertEquals("user1", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void testLoginNegative() {
        LoginRequest request = new LoginRequest("no-user", "wrong-pass");
        assertThrows(DataAccessException.class, () -> userService.login(request));
    }


    //test logout
    @Test
    public void testLogoutPositive() throws DataAccessException {
        RegisterRequest regRequest = new RegisterRequest("user2", "pass2", "u2@example.com");
        RegisterResult result = userService.register(regRequest);
        assertDoesNotThrow(() -> userService.logout(result.authToken()));
    }

    @Test
    public void testLogoutNegative() {
        assertThrows(DataAccessException.class, () -> userService.logout("fake-token"));
    }
}
