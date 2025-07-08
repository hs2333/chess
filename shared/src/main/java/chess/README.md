# README-chess

[Phase 2 Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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-CUCoX+5KUC-OLpgIAA)

## Code
```uml
actor Client
participant Server
participant Service
participant DataAccess
database db

group #navy Registration #white
Client -> Server: [POST] /user\n{username, password, email}
Server -> Service: registerNewUser(username, password, email)
Service -> DataAccess: findUserByName(username)
DataAccess -> db: SELECT * FROM user WHERE username = ?
DataAccess --> Service: null
Service -> DataAccess: insertUser(username, password, email)
DataAccess -> db: INSERT INTO user VALUES (...)
Service -> DataAccess: generateAuthToken(username)
DataAccess -> db: INSERT INTO auth (username, token) VALUES (...)
DataAccess --> Service: newAuthToken
Service --> Server: newAuthToken
Server --> Client: 200\n{authToken: newAuthToken}
end

group #orange Login #white
Client -> Server: [POST] /session\n{username, password}
Server -> Service: authenticateLogin(username, password)
Service -> DataAccess: validateUserCredentials(username, password)
DataAccess -> db: SELECT password FROM user WHERE username = ?
DataAccess --> Service: credentialsValid
Service -> DataAccess: storeAuthToken(username)
DataAccess -> db: INSERT INTO auth VALUES (username, token)
DataAccess --> Service: authToken
Service --> Server: {username, authToken}
Server --> Client: 200\n{username, authToken}
end

group #green Logout #white
Client -> Server: [DELETE] /session\nauthToken
Server -> Service: processLogout(authToken)
Service -> DataAccess: removeAuthToken(authToken)
DataAccess -> db: DELETE FROM auth WHERE token = ?
DataAccess --> Service: done
Service --> Server: success
Server --> Client: 200\n{"message":"Logged out"}
end

group #red List Games #white
Client -> Server: [GET] /game\nauthToken
Server -> Service: retrieveGames(authToken)
Service -> DataAccess: getUsernameFromToken(authToken)
DataAccess -> db: SELECT username FROM auth WHERE token = ?
DataAccess --> Service: user
Service -> DataAccess: fetchGamesByUser(user)
DataAccess -> db: SELECT * FROM game WHERE whiteUsername = user OR blackUsername = user
DataAccess --> Service: [games]
Service --> Server: [games]
Server --> Client: 200\n{"games": [...]}
end

group #purple Create Game #white
Client -> Server: [POST] /game\nauthToken\n{gameName}
Server -> Service: startGame(authToken, gameName)
Service -> DataAccess: getUser(authToken)
DataAccess -> db: SELECT username FROM auth WHERE token = ?
DataAccess --> Service: username
Service -> DataAccess: createGameEntry(gameName, createdBy)
DataAccess -> db: INSERT INTO game (gameName, whiteUsername) VALUES (...)
DataAccess --> Service: newGameID
Service --> Server: {gameID: newGameID}
Server --> Client: 200\n{gameID: newGameID}
end

group #yellow Join Game #black
Client -> Server: [PUT] /game\nauthToken\n{color, gameID}
Server -> Service: requestJoinGame(authToken, color, gameID)
Service -> DataAccess: getPlayerFromAuth(authToken)
DataAccess -> db: SELECT username FROM auth WHERE token = ?
DataAccess --> Service: playerName
Service -> DataAccess: lookupGame(gameID)
DataAccess -> db: SELECT * FROM game WHERE id = gameID
Service -> DataAccess: assignPlayerToColor(gameID, color, playerName)
DataAccess -> db: UPDATE game SET colorUsername = playerName WHERE id = gameID
DataAccess --> Service: confirmed
Service --> Server: joinSuccess
Server --> Client: 200\n{"message":"Joined"}
end

group #gray Clear application #white
Client -> Server: [DELETE] /db
Server -> Service: wipeAllData()
Service -> DataAccess: clearTables()
DataAccess -> db: DELETE FROM user
DataAccess -> db: DELETE FROM auth
DataAccess -> db: DELETE FROM game
DataAccess --> Service: cleared
Service --> Server: success
Server --> Client: 200\n{"message":"Database cleared"}
end
```
