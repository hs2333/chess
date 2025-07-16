package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

public class GameService {
    private final MemoryGameDAO gameDAO;
    private final MemoryAuthDAO authDAO;

    public GameService(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        //games
        this.gameDAO = gameDAO;
        //authentication tokens
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

    public ListGamesResult listGames(String token) throws DataAccessException {
        if (token == null || !authDAO.isValidToken(token)) {
            throw new DataAccessException("Error: unauthorized");
        }
        return new ListGamesResult(gameDAO.listGames().values());
    }

    public void joinGame(String token, JoinGameRequest req) throws DataAccessException {
        //check token valid
        if (token == null || !authDAO.isValidToken(token)) {
            throw new DataAccessException("Error: unauthorized");
        }
        //check join request
        if (req.playerColor() == null || req.gameID() <= 0) {
            throw new DataAccessException("Error: bad request");
        }

        String username = authDAO.getAuth(token).username();
        gameDAO.joinGame(req.gameID(), username, req.playerColor());
    }


}
