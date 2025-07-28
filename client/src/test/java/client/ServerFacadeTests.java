package client;

import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import java.util.UUID;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);

        //new ServerFacade
        facade = new ServerFacade("http://localhost:" + port);

        System.out.println("Started test HTTP server on port " + port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }


    //register
    @Test
    public void testRegisterPositive() {
        String user = "user_" + System.currentTimeMillis();
        boolean result = facade.register(user, "pass", user + "test@email.com");
        assertTrue(result);
    }
    @Test
    public void testRegisterNegative() {
        String user = "repeat_user";
        facade.register(user, "pass", user + "test@email.com");
        boolean result = facade.register(user, "pass", user + "test@email.com");
        assertFalse(result);
    }

    //login
    @Test
    public void testLoginPositive() {
        String user = "user_" + System.currentTimeMillis();
        facade.register(user, "pass", user + "test@email.com");
        boolean result = facade.login(user, "pass");
        assertTrue(result);
    }
    @Test
    public void testLoginNegative() {
        boolean result = facade.login("wrong_user", "wrong_pass");
        assertFalse(result);
    }

    //logout
    @Test
    public void testLogoutPositive() {
        String user = "user_" + System.currentTimeMillis();
        facade.register(user, "pass", user + "test@email.com");
        boolean result = facade.logout();
        assertTrue(result);
    }
    @Test
    public void testLogoutNegative() {
        facade.authToken = null;
        boolean result = facade.logout();
        assertFalse(result);
    }

    //create game
    @Test
    public void testCreateGamePositive() {
        String user = "user_" + System.currentTimeMillis();
        facade.register(user, "pass", user + "test@email.com");
        int gameId = facade.createGame("TestGame");
        assertTrue(gameId > 0);
    }
    @Test
    public void testCreateGameNegative() {
        facade.authToken = null;
        int gameId = facade.createGame("InvalidGame");
        assertEquals(-1, gameId);
    }

    //list game
    @Test
    public void testListGamesPositive() {
        String user = "user_" + System.currentTimeMillis();
        facade.register(user, "pass", user + "test@email.com");
        int gameId = facade.createGame("GameToList");
        HashSet<GameData> games = facade.listGames();
        assertNotNull(games);
        assertTrue(games.stream().anyMatch(g->g.gameID() == gameId));
    }
    @Test
    public void testListGamesNegative() {
        facade.authToken = null;
        HashSet<GameData> games = facade.listGames();
        assertTrue(games.isEmpty());
    }

    //join game
    @Test
    public void testJoinGamePositive() {
        String user = "user_" + System.currentTimeMillis();
        facade.register(user, "pass", user + "test@email.com");
        int gameId = facade.createGame("JoinGame");
        boolean result = facade.joinGame(gameId, "WHITE");
        assertTrue(result);
    }
    @Test
    public void testJoinGameNegative() {
        facade.authToken = null;
        boolean result = facade.joinGame(999, "BLACK");
        assertFalse(result);
    }
}
