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
        }
        else if (exception.getMessage().contains("unauthorized"))
        {res.status(401);}
        else if (exception.getMessage().contains("taken"))
        {res.status(403);}
        else
        {res.status(400);}
        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "Error occurred.";
        return serializer.toJson(Map.of("message", "Error: " + errorMessage));
    }
}
