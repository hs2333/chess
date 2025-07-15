package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

public class GameService {
    private final MemoryGameDAO gameDAO;
    private final MemoryAuthDAO authDAO;

    public GameService(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(String token, CreateGameRequest req) throws DataAccessException {
        if (token == null || !authDAO.isValidToken(token)) {
            throw new DataAccessException("Error: unauthorized");
        }

        if (req.gameName() == null || req.gameName().isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        var username = authDAO.getAuth(token).username();
        var newGame = new GameData(0, null, null, req.gameName(), new ChessGame());

        int id = gameDAO.createGame(newGame);
        return new CreateGameResult(id);
    }
}
