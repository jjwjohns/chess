package server;

import dataAccess.DataMemory;
import model.Authtoken;
import model.User;
import service.ChessService;
import spark.*;
import com.google.gson.Gson;

public class Server {
    public DataMemory dataAccess = new DataMemory();
    public ChessService service = new ChessService(dataAccess);

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.externalLocation("src/resources/web");

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

    private Object register(Request req, Response res) throws Exception{
        var user = new Gson().fromJson(req.body(), User.class);
        if(user.username() == null || user.email() == null || user.password() == null){
            res.status(400);
            res.body("{\"message\": \"Error: bad request\"}");
            return res.body();
        }
        Authtoken auth = this.service.register(user);
        if (auth == null) {
            res.status(403);
            res.body("{\"message\": \"Error: already taken\"}");
            return res.body();
        }

        res.status(200);
        return new Gson().toJson(auth);
    }

    private Object clear(Request req, Response res) throws Exception{
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
