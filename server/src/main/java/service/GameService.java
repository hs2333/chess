package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

public class GameService {
    private final GameDAO gameDAO = DataAccessFactory.getGameDAO();
    private final AuthDAO authDAO = DataAccessFactory.getAuthDAO();

    public CreateGameResult createGame(String token, CreateGameRequest req) throws DataAccessException {
        if (token == null || !authDAO.isValidToken(token)) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (req.gameName() == null || req.gameName().isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        var newGame = new GameData(0, null, null, req.gameName(), new ChessGame());
        GameData storedGame = gameDAO.createGame(newGame);
        return new CreateGameResult(storedGame.gameID());
    }

    public ListGamesResult listGames(String token) throws DataAccessException {
        if (token == null || !authDAO.isValidToken(token)) {
            throw new DataAccessException("Error: unauthorized");
        }
        return new ListGamesResult(gameDAO.listGames());
    }

    public void joinGame(String token, JoinGameRequest req) throws DataAccessException {
        if (token == null || !authDAO.isValidToken(token)) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (req.playerColor() == null || req.gameID() <= 0) {
            throw new DataAccessException("Error: bad request");
        }

        String username = authDAO.getAuth(token).username();
        GameData game = gameDAO.getGame(req.gameID());

        if (game == null) {
            throw new DataAccessException("Error: game not found");
        }

        if ("WHITE".equalsIgnoreCase(req.playerColor())) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if ("BLACK".equalsIgnoreCase(req.playerColor())) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            throw new DataAccessException("Error: invalid color");
        }

        gameDAO.updateGame(game);
    }
}
