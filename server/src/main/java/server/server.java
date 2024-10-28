package server;

import chess.ChessGame;
import dataAccess.DataMemory;
import model.*;
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

//        Spark.get("/", (req, res) -> "CS 240 Chess Server Web API");
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
        var login = new Gson().fromJson(req.body(), LoginRequest.class);
        Authtoken auth = this.service.login(login);

        if (auth == null){
            res.status(401);
            res.body("{\"message\": \"Error: unauthorized\"}");
            return res.body();
        }
        res.status(200);
        return new Gson().toJson(auth);
    }

    private Object logout(Request req, Response res) throws Exception {
        String authToken = req.headers("Authorization");
        if (dataAccess.authorize(authToken)){
            this.service.logout(authToken);
            res.status(200);
            return "";
        }
        res.status(401);
        res.body("{\"message\": \"Error: unauthorized\"}");
        return res.body();
    }

    private Object createGame(Request req, Response res) throws Exception {
        CreateRequest createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
        String authToken = req.headers("Authorization");
        if (dataAccess.authorize(authToken)){
            CreateResult result = this.service.createGame(createRequest);
            res.status(200);
            return new Gson().toJson(result);
        }
        res.status(401);
        res.body("{\"message\": \"Error: unauthorized\"}");
        return res.body();
    }

    private Object listGames(Request req, Response res) throws Exception {
        String authToken = req.headers("Authorization");
        if (dataAccess.authorize(authToken)){
            ListResult result = this.service.listGames();
            res.status(200);
            return new Gson().toJson(result);
        }
        res.status(401);
        res.body("{\"message\": \"Error: unauthorized\"}");
        return res.body();
    }

    private Object joinGame(Request req, Response res) throws Exception {
        JoinRequest joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        String authToken = req.headers("Authorization");
        if (dataAccess.authorize(authToken)){
            if (joinRequest.gameID() == null || joinRequest.playerColor() == null){
                res.status(400);
                res.body("{\"message\": \"Error: bad request (null values)\"}");
                return res.body();
            }
            if (joinRequest.playerColor() == ChessGame.TeamColor.WHITE){
                if (dataAccess.getGame(joinRequest.gameID()).whiteUsername() != null){
                    res.status(403);
                    res.body("{\"message\": \"Error: already taken\"}");
                    return res.body();
                }
            }
            else if (joinRequest.playerColor() == ChessGame.TeamColor.BLACK){
                if (dataAccess.getGame(joinRequest.gameID()).blackUsername() != null){
                    res.status(403);
                    res.body("{\"message\": \"Error: already taken\"}");
                    return res.body();
                }
            }
            this.service.joinGame(joinRequest, authToken);
            res.status(200);
            return "";
        }
        res.status(401);
        res.body("{\"message\": \"Error: unauthorized\"}");
        return res.body();
    }
}
