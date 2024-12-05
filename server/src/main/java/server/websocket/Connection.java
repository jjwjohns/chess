package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String user;
    public Session session;
    public Integer gameID;

    public Connection(String user, Integer gameID, Session session) {
        this.user = user;
        this.session = session;
        this.gameID = gameID;
    }

    public void send(String msg) throws IOException {
        String jsonMessage = new Gson().toJson(msg);
        session.getRemote().sendString(msg);
    }
}