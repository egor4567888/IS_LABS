package org.example.ws;

import javax.ejb.Singleton;
import javax.websocket.Session;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.example.model.Person;

@Singleton
public class Broadcaster {
    private Set<Session> sessions = new CopyOnWriteArraySet<>();

    public void register(Session s) { sessions.add(s); }
    public void unregister(Session s) { sessions.remove(s); }

    public void broadcastCreate(Person p) {
        broadcast("create", Json.createObjectBuilder().add("id", p.getId()).add("name", p.getName()).build());
    }

    public void broadcastUpdate(Person p) {
        broadcast("update", Json.createObjectBuilder().add("id", p.getId()).add("name", p.getName()).build());
    }

    public void broadcastDelete(Long id) {
        broadcast("delete", Json.createObjectBuilder().add("id", id).build());
    }

    private void broadcast(String type, JsonObject data) {
        JsonObjectBuilder msg = Json.createObjectBuilder();
        msg.add("type", type);
        msg.add("data", data);

        for (Session s : sessions) {
            try {
                s.getBasicRemote().sendText(msg.build().toString());
            } catch (Exception ignored) {}
        }
    }
}
