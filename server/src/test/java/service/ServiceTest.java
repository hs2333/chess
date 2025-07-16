
package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
    private UserService userService;
    private GameService gameService;
    private ClearService clearService;
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;

    //set up
    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }


    //USER
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


    //CLEAR
    //test clear

    //GAME
    //test create game
    @Test
    public void testCreateGamePositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("creator", "pass", "email");
        RegisterResult regResult = userService.register(request);

        CreateGameRequest gameRequest = new CreateGameRequest("NewGame");
        CreateGameResult result = gameService.createGame(regResult.authToken(), gameRequest);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    public void testCreateGameNegative() {
        CreateGameRequest badRequest = new CreateGameRequest("GameName");
        assertThrows(DataAccessException.class, () -> gameService.createGame("bad-token", badRequest));
    }

    // ---- JOIN GAME ----
    @Test
    public void testJoinGamePositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("joiner", "pass", "email");
        RegisterResult regResult = userService.register(request);

        CreateGameResult created = gameService.createGame(regResult.authToken(), new CreateGameRequest("JoinGame"));
        JoinGameRequest joinRequest = new JoinGameRequest("WHITE", created.gameID());

        assertDoesNotThrow(() -> gameService.joinGame(regResult.authToken(), joinRequest));
    }

    @Test
    public void testJoinGameNegative() throws DataAccessException {
        JoinGameRequest badRequest = new JoinGameRequest("BLACK", 999); // game doesn't exist
        assertThrows(DataAccessException.class, () -> gameService.joinGame("fake-token", badRequest));
    }

    // ---- LIST GAMES ----
    @Test
    public void testListGamesPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("viewer", "pass", "email");
        RegisterResult regResult = userService.register(request);

        gameService.createGame(regResult.authToken(), new CreateGameRequest("ListableGame"));
        ListGamesResult result = gameService.listGames(regResult.authToken());

        assertNotNull(result.games());
        assertFalse(result.games().isEmpty());
    }

    @Test
    public void testListGamesNegative() {
        assertThrows(DataAccessException.class, () -> gameService.listGames("invalid-token"));
    }
}
