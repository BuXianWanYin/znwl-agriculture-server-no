package com.frog.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;


/**
 * WebSocket 客户端
 */
@Component
@ClientEndpoint
public class WebSocketClient {

    private static final Log log = LogFactory.getLog(WebSocketClient.class);

    private Session session;

    @OnOpen
    public void open(Session session) {

        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {

        // 这里可以处理来自 WebSocketServer 的消息
        WebSocketServer.sendToAllClient(message);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        onClose();
        t.printStackTrace();
    }

    @OnClose
    public void onClose() {
        if (this.session != null && this.session.isOpen()) {
            try {
                this.session.close();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    public void send(String message) {
        if (this.session != null && this.session.isOpen()) { // 防止 session 为 null
            this.session.getAsyncRemote().sendText(message);
        } else {
           // log.warn("WebSocket session is not open!");
        }
    }

    public void send(ByteBuffer message) {
        if (this.session != null && this.session.isOpen()) { // 防止 session 为 null
            this.session.getAsyncRemote().sendBinary(message);
        } else {
          //  log.warn("WebSocket session is not open!");
        }
    }
}