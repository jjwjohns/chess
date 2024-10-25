package server;

import dataAccess.DataAccessException;
import model.User;
import service.ChessService;
import spark.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.post("/user", this::register);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) throws Exception {
        var user = new Gson().fromJson(req.body(), User.class);
//        ChessService yes = new ChessService();
        ChessService.addUser();
        return new Gson().toJson(user);
    }
}
