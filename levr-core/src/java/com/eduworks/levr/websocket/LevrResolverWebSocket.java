package com.eduworks.levr.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/ws/custom")
public class LevrResolverWebSocket
{
	public LevrResolverWebSocket()
	{
		
	}

    @OnOpen
    public void onOpen(Session session) throws IOException {
        session.getBasicRemote().sendText("onOpen");
    }

	@OnMessage
	public void onMessage(Session session, String message)
	{
		session.getAsyncRemote().sendText(message);
	}
	
    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {

    }
	
}
