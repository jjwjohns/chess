package server;

import dataAccess.DataMemory;
import model.Authtoken;
import model.User;
import service.ChessService;
import spark.*;
import com.google.gson.Gson;

public class Server {
    DataMemory dataAccess = new DataMemory();
    private final ChessService service = new ChessService(dataAccess);

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.get("/", (req, res) -> "CS 240 Chess Server Web API");
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
        var user = new Gson().fromJson(req.body(), User.class);
        Authtoken auth = this.service.register(user);
        return new Gson().toJson(auth);
    }

    private Object clear(Request req, Response res) throws Exception {
        this.service.clear();
        res.status(200);
        return "";
    }


    private Object login(Request req, Response res) throws Exception {
        throw new Exception("not implemented (server)");
    }

    private Object logout(Request req, Response res) throws Exception {
        throw new Exception("not implemented (server)");
    }

    private Object listGames(Request req, Response res) throws Exception {
        throw new Exception("not implemented (server)");
    }

    private Object createGame(Request req, Response res) throws Exception {
        throw new Exception("not implemented (server)");
    }

    private Object joinGame(Request req, Response res) throws Exception {
        throw new Exception("not implemented (server)");
    }
}
