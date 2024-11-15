package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public Authtoken register(String... params) throws Exception{
        var path = "/user";
        String username = params[0];
        String pass = params[1];
        String email = params[2];
        User user = new User(username, pass, email);
        return this.makeRequest("POST", path, user, null, Authtoken.class);
    }

    public Authtoken login(String... params) throws Exception {
        var path = "/session";
        String username = params[0];
        String pass = params[1];
        LoginRequest login = new LoginRequest(username, pass);
        return this.makeRequest("POST", path, login, null, Authtoken.class);
    }

    public void logout(Authtoken auth) throws Exception {
        if (auth == null || auth.authToken() == null){
            throw new Exception("authtoken cannot be null");
        }
        var path = "/session";
        this.makeRequest("DELETE", path, null, auth.authToken(), null);
    }

    public void create(Authtoken auth, String... params) throws Exception {
        var path = "/game";
        String gamename = params[0];
        CreateRequest request = new CreateRequest(gamename);
        this.makeRequest("POST", path, request, auth.authToken(), null);
    }

    public ListResult list(Authtoken auth) throws Exception {
        var path = "/game";
        return this.makeRequest("GET", path, null, auth.authToken(), ListResult.class);
    }

    public void play(Authtoken auth, int id, ChessGame.TeamColor color) throws Exception {
        var path = "/game";
        JoinRequest request = new JoinRequest(color, id);
        this.makeRequest("PUT", path, request, auth.authToken(), null);
    }

    public String observe(int id) throws Exception {
        if (id < 0) {
            throw new Exception("Invalid board ID");
        }
        return "Observing board " + id;
    }

    private <T> T makeRequest(String method, String path, Object request, String authtoken, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authtoken != null && !authtoken.isEmpty()){
                http.setRequestProperty("Authorization", authtoken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
        throw new Exception("Request failed: " + ex.getMessage(), ex);
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new Exception("http failure:" + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}