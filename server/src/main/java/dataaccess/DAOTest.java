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

    // ---------- UserDAO Tests ----------

    @Test
    public void insertUser_positive() throws DataAccessException {
        var user = new UserData("alice", "pass123", "a@x.com");
        userDAO.insertUser(user);
        assertNotNull(userDAO.getUser("alice"));
    }

    @Test
    public void insertUser_negative_duplicate() throws DataAccessException {
        var user = new UserData("bob", "pw", "b@x.com");
        userDAO.insertUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.insertUser(user));
    }

    @Test
    public void validateUser_positive() throws DataAccessException {
        var hashed = org.mindrot.jbcrypt.BCrypt.hashpw("secret", org.mindrot.jbcrypt.BCrypt.gensalt());
        var user = new UserData("carol", hashed, "c@x.com");
        userDAO.insertUser(user);
        assertTrue(userDAO.validateUser("carol", "secret"));
    }

    @Test
    public void validateUser_wrongPassword() throws DataAccessException {
        var hashed = org.mindrot.jbcrypt.BCrypt.hashpw("secret", org.mindrot.jbcrypt.BCrypt.gensalt());
        var user = new UserData("dave", hashed, "d@x.com");
        userDAO.insertUser(user);
        assertFalse(userDAO.validateUser("dave", "wrong"));
    }

    @Test
    public void validateUser_userNotFound() {
        assertThrows(DataAccessException.class, () -> userDAO.validateUser("ghost", "pw"));
    }

    // ---------- AuthDAO Tests ----------

    @Test
    public void insertAuth_positive() throws DataAccessException {
        userDAO.insertUser(new UserData("user", "p", "e")); // FK dependency
        var token = new AuthData("token123", "user");
        authDAO.insertAuth(token);
        assertTrue(authDAO.isValidToken("token123"));
    }

    @Test
    public void insertAuth_duplicate() throws DataAccessException {
        userDAO.insertUser(new UserData("user", "p", "e"));
        var token = new AuthData("tok", "user");
        authDAO.insertAuth(token);
        assertThrows(DataAccessException.class, () -> authDAO.insertAuth(token));
    }

    @Test
    public void getAuth_notFound() throws DataAccessException {
        assertNull(authDAO.getAuth("nope"));
    }

    @Test
    public void deleteAuth_positive() throws DataAccessException {
        userDAO.insertUser(new UserData("u", "p", "e"));
        authDAO.insertAuth(new AuthData("del", "u"));
        authDAO.deleteAuth("del");
        assertFalse(authDAO.isValidToken("del"));
    }

    // ---------- GameDAO Tests ----------

    @Test
    public void createGame_positive() throws DataAccessException {
        var game = new GameData(0, null, null, "New Game", new ChessGame());
        var created = gameDAO.createGame(game);
        assertNotNull(gameDAO.getGame(created.gameID()));
    }

    @Test
    public void listGames_positive() throws DataAccessException {
        gameDAO.createGame(new GameData(0, null, null, "G1", new ChessGame()));
        gameDAO.createGame(new GameData(0, null, null, "G2", new ChessGame()));
        Collection<GameData> list = gameDAO.listGames();
        assertEquals(2, list.size());
    }

    @Test
    public void updateGame_whitePlayer() throws DataAccessException {
        userDAO.insertUser(new UserData("someone", "password", "someone@email.com"));

        var game = gameDAO.createGame(new GameData(0, null, null, "G", new ChessGame()));

        var updated = new GameData(game.gameID(), "someone", null, game.gameName(), game.game());
        gameDAO.updateGame(updated);
        assertEquals("someone", gameDAO.getGame(game.gameID()).whiteUsername());
    }


    @Test
    public void getGame_notFound() throws DataAccessException {
        assertNull(gameDAO.getGame(9999));
    }
}
