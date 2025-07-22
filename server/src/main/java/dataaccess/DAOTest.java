package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;



public class DAOTest {

    private final UserDAO userDAO = new MySqlUserDAO();
    private final AuthDAO authDAO = new MySqlAuthDAO();
    private final GameDAO gameDAO = new MySqlGameDAO();

    @BeforeAll
    static void setUp() {
        try {
            DatabaseInitializer.initialize();
        } catch (DataAccessException e) {
            fail("Failed to initialize database: " + e.getMessage());
        }
    }


    @BeforeEach
    public void clearAll() throws DataAccessException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }


    //UserDAO Tests
    @Test
    public void insertUserPositive() throws DataAccessException {
        var user = new UserData("alice", "pass123", "a@x.com");
        userDAO.insertUser(user);
        assertNotNull(userDAO.getUser("alice"));
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        assertNull(userDAO.getUser("nonexistent"));
    }

    @Test
    public void insertUserNegative() throws DataAccessException {
        var user = new UserData("bob", "pw", "b@x.com");
        userDAO.insertUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.insertUser(user));
    }

    @Test
    public void validateUserPositive() throws DataAccessException {
        var hashed = org.mindrot.jbcrypt.BCrypt.hashpw("secret", org.mindrot.jbcrypt.BCrypt.gensalt());
        var user = new UserData("carol", hashed, "c@x.com");
        userDAO.insertUser(user);
        assertTrue(userDAO.validateUser("carol", "secret"));
    }
    @Test
    public void validateUseNegativeWrongPassword() throws DataAccessException {
        var hashed = org.mindrot.jbcrypt.BCrypt.hashpw("secret", org.mindrot.jbcrypt.BCrypt.gensalt());
        var user = new UserData("dave", hashed, "d@x.com");
        userDAO.insertUser(user);
        assertFalse(userDAO.validateUser("dave", "wrong"));
    }
    @Test
    public void validateUserNegativeNotFound() {
        assertThrows(DataAccessException.class, () -> userDAO.validateUser("ghost", "pw"));
    }


    //AuthDAO Tests
    @Test
    public void insertAuthPositive() throws DataAccessException {
        userDAO.insertUser(new UserData("user", "p", "e")); // FK dependency
        var token = new AuthData("token123", "user");
        authDAO.insertAuth(token);
        assertTrue(authDAO.isValidToken("token123"));
    }
    @Test
    public void insertAuthNegative() throws DataAccessException {
        userDAO.insertUser(new UserData("user", "p", "e"));
        var token = new AuthData("tok", "user");
        authDAO.insertAuth(token);
        assertThrows(DataAccessException.class, () -> authDAO.insertAuth(token));
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        userDAO.insertUser(new UserData("user", "p", "e"));
        var expected = new AuthData("validToken123", "user");
        authDAO.insertAuth(expected);
        var actual = authDAO.getAuth("validToken123");
        assertNotNull(actual);
        assertEquals(expected.authToken(), actual.authToken());
        assertEquals(expected.username(), actual.username());
    }
    @Test
    public void getAuthNegative() throws DataAccessException {
        assertNull(authDAO.getAuth("nope"));
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        userDAO.insertUser(new UserData("u", "p", "e"));
        authDAO.insertAuth(new AuthData("del", "u"));
        authDAO.deleteAuth("del");
        assertFalse(authDAO.isValidToken("del"));
    }

    @Test
    public void isValidTokenNegative() throws DataAccessException {
        assertFalse(authDAO.isValidToken("bad_token"));
    }

    @Test
    public void deleteAuthNegative() throws DataAccessException {
        // Should not throw, just a no-op
        assertDoesNotThrow(() -> authDAO.deleteAuth("not_there"));
    }


    //GameDAO
    @Test
    public void createGamePositive() throws DataAccessException {
        var game = new GameData(0, null, null, "New Game", new ChessGame());
        var created = gameDAO.createGame(game);
        assertNotNull(gameDAO.getGame(created.gameID()));
    }
    @Test
    public void createGame_negative() {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        gameDAO.createGame(new GameData(0, null, null, "G1", new ChessGame()));
        gameDAO.createGame(new GameData(0, null, null, "G2", new ChessGame()));
        Collection<GameData> list = gameDAO.listGames();
        assertEquals(2, list.size());
    }

    @Test
    public void updateGamePositiveWhitePlayer() throws DataAccessException {
        userDAO.insertUser(new UserData("someone", "password", "someone@email.com"));

        var game = gameDAO.createGame(new GameData(0, null, null, "G", new ChessGame()));

        var updated = new GameData(game.gameID(), "someone", null, game.gameName(), game.game());
        gameDAO.updateGame(updated);
        assertEquals("someone", gameDAO.getGame(game.gameID()).whiteUsername());
    }
    @Test
    public void updateGameNegative() throws DataAccessException {
        GameData nonexistent = new GameData(9999, "u", null, "x", new ChessGame());
        // Should not throw, but won't affect anything
        assertDoesNotThrow(() -> gameDAO.updateGame(nonexistent));
    }


    @Test
    public void getGameNegative() throws DataAccessException {
        assertNull(gameDAO.getGame(9999));
    }
}
