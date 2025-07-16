package server;

import spark.Response;

import java.util.Map;
import com.google.gson.Gson;

public class serverHelp {
    public static String handleError(Response res, Exception e) {
        if (e.getMessage().contains("unauthorized")) {
            res.status(401);
        } else if (e.getMessage().contains("taken")) {
            res.status(403);
        } else {
            res.status(400);
        }
        return new Gson().toJson(Map.of("message", e.getMessage()));
    }
}
