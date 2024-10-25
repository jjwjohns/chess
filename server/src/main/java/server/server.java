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
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) throws Exception {
//        var user = new Gson().fromJson(req.body(), User.class);
////        ChessService yes = new ChessService();
//        ChessService.addUser();
//        return new Gson().toJson(user);
        throw new Exception("not implemented");
    }

    private Object clear(Request req, Response res) throws Exception {
        throw new Exception("not implemented");
    }

    private Object login(Request req, Response res) throws Exception {
        throw new Exception("not implemented");
    }

    private Object logout(Request req, Response res) throws Exception {
        throw new Exception("not implemented");
    }

    private Object listGames(Request req, Response res) throws Exception {
        throw new Exception("not implemented");
    }

    private Object createGame(Request req, Response res) throws Exception {
        throw new Exception("not implemented");
    }

    private Object joinGame(Request req, Response res) throws Exception {
        throw new Exception("not implemented");
    }
}
