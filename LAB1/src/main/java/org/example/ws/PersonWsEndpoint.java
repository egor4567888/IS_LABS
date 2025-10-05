package org.example.ws;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.inject.Inject;

@ServerEndpoint("/ws/persons")
public class PersonWsEndpoint {

    @Inject
    private Broadcaster broadcaster;

    @OnOpen
    public void onOpen(Session session) {
        broadcaster.register(session);
    }

    @OnClose
    public void onClose(Session session) {
        broadcaster.unregister(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        // можно обрабатывать клиентские сообщения, сейчас - noop
    }

    @OnError
    public void onError(Session session, Throwable t) {
        // log
    }
}