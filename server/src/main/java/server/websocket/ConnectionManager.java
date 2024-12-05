package server.websocket;

import com.google.gson.Gson;
import model.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String user, Integer gameID, Session session) {
        System.out.println("add (server)");
        var connection = new Connection(user, gameID, session);
        connections.put(user, connection);
    }

    public void broadcast(String userToIgnore, Integer gameID, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (Objects.equals(c.gameID, gameID) && !Objects.equals(c.user, userToIgnore)) {
                    c.send(new Gson().toJson(message));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.user);
        }
    }

    public void remove(String user) {
        connections.remove(user);
    }

}
