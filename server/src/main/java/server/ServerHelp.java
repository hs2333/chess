package server;

import dataaccess.DataAccessException;
import spark.Response;

import java.sql.SQLException;
import java.util.Map;
import com.google.gson.Gson;

public class ServerHelp {
    public static String handleDataAccessException(DataAccessException exception, Response res, Gson serializer) {
        if (exception.getCause() instanceof SQLException) {
            res.status(500);
        } else {
            res.status(401);
        }
        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "An unknown error occurred.";
        return serializer.toJson(Map.of("message", "Error: " + errorMessage));
    }
}
