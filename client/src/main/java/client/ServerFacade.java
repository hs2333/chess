package client;

import com.google.gson.Gson;
import model.GameData;
import model.GamesList;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class ServerFacade {

    private String baseURL = "http://localhost:8080";
    public String authToken;

    public ServerFacade() {}
    //url instead of port num
    public ServerFacade(String url) {
        baseURL = url;
    }

    //reguster
    public boolean register(String username, String password, String email) {
        var body = Map.of("username", username, "password", password, "email", email);
        var jsonBody = new Gson().toJson(body);
        //can't use the same info to register
        Map resp = request("POST", "/user", jsonBody);
        if (resp.containsKey("Error")) {return false;}
        //check handlers if authToken
        authToken = (String) resp.get("authToken");

        return true;
    }

    //login
    public boolean login(String username, String password) {
        var body = Map.of("username", username, "password", password);
        var jsonBody = new Gson().toJson(body);

        Map resp = request("POST", "/session", jsonBody);
        if (resp.containsKey("Error")) {return false;}
        authToken = (String) resp.get("authToken");
        return true;
    }

    //logout
    public boolean logout() {
        Map resp = request("DELETE", "/session");
        if (resp.containsKey("Error")) {return false;}
        authToken = null;
        return true;
    }

    //create game
    public int createGame(String gameName) {
        var body = Map.of("gameName", gameName);
        var jsonBody = new Gson().toJson(body);

        Map resp = request("POST", "/game", jsonBody);

        if (resp.containsKey("Error"))
        {return -1;}

        return ((Double) resp.get("gameID")).intValue(); // safe cast
    }

    //list games
    public HashSet<GameData> listGames() {
        String resp = requestString("GET", "/game");
        if (resp.startsWith("Error")) {
            return new HashSet<>();}

        GamesList gamesList = new Gson().fromJson(resp, GamesList.class);
        //compare to GameData-->single game, GamesLists contain multiple games-->HashSet
        return gamesList.games() != null  ?  gamesList.games()  :  new HashSet<>();

    }

    //join game
    public boolean joinGame(int gameId, String playerColor) {
        Map<String, Object> body = new HashMap<>();
        body.put("gameID", gameId);
        if (playerColor != null) {
            body.put("playerColor", playerColor);
        }
        var jsonBody = new Gson().toJson(body);
        Map resp = request("PUT", "/game", jsonBody);
        return !resp.containsKey("Error");
    }

    //check clearService if error
    public boolean clear() {
        Map resp = request("POST", "/db");
        return !resp.containsKey("Error");
    }






    private Map request(String method, String endpoint) {
        return request(method, endpoint, null);
    }
    private Map request(String method, String endpoint, String body) {
        try {
            URI uri = new URI(baseURL + endpoint);
            HttpURLConnection http = (HttpURLConnection)uri.toURL().openConnection();
            http.setRequestMethod(method);
            http.setRequestProperty("Accept", "application/json");

            //check authToken and body
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }
            if (body != null) {
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");
                try (var outputStream = http.getOutputStream()) {
                    outputStream.write(body.getBytes());
                }
                //catch
            }

            http.connect();


            InputStreamReader reader;
            if (http.getResponseCode() >= 400) {
                reader = new InputStreamReader(http.getErrorStream());
                Map error = new Gson().fromJson(reader, Map.class);
                return Map.of("Error", error.getOrDefault("message", "unknown error"));
            } else {
                reader = new InputStreamReader(http.getInputStream());
                return new Gson().fromJson(reader, Map.class);
            }
        } catch (URISyntaxException | IOException exception) {
            return Map.of("Error", exception.getMessage());
        }
    }




    private String requestString(String method, String endpoint) {
        return requestString(method, endpoint, null);
    }
    private String requestString(String method, String endpoint, String body) {
        try {
            URI uri = new URI(baseURL + endpoint);
            HttpURLConnection http = (HttpURLConnection)uri.toURL().openConnection();
            http.setRequestMethod(method);
            http.setRequestProperty("Accept", "application/json");

            //check authToken and body
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }
            if (body != null) {
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");
                try (var outputStream = http.getOutputStream()) {
                    outputStream.write(body.getBytes());
                }
            }

            http.connect();


            InputStreamReader reader;
            if (http.getResponseCode() >= 400) {
                reader = new InputStreamReader(http.getErrorStream());
            } else {
                reader = new InputStreamReader(http.getInputStream());
            }
            return readerToString(reader);

        } catch (URISyntaxException | IOException exception) {
            return "Error: " + exception.getMessage();
        }
    }


    private String readerToString(InputStreamReader reader) {
        //string buffer
        StringBuilder sb = new StringBuilder();
        try {
            //read characters
            for (int ch; (ch = reader.read()) != -1; ) {
                sb.append((char) ch);
            }
            return sb.toString();
        } catch (IOException exception) {
            return "Error reading response: " + exception.getMessage();
        }
    }
}

