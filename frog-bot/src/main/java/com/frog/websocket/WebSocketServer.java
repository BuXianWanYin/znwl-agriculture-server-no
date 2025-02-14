package com.frog.websocket;

import com.frog.config.BotConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket服务
 */
@Component
@ServerEndpoint("/ws/asr/{sid}")
public class WebSocketServer {

    private static Map<String, Session> sessionMap = new HashMap<>();
    private static final Log log = LogFactory.getLog(WebSocketServer.class);

    private WebSocketClient webSocketClient = new WebSocketClient(); // 添加客户端实例变量

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("客户端：" + sid + "建立连接");
        sessionMap.put(sid, session);

        try {
            // 这里连接到 WebSocketClient，并在连接成功后保存 Session
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            Session sessionClient = container.connectToServer(webSocketClient, URI.create(BotConfig.getAsrUrl()));
            webSocketClient.open(sessionClient); // 这里记得调用 open 方法




         //   log.info("Connected to client WebSocket");
        } catch (DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
      //  log.info("收到来自客户端：" + sid + "的信息:" + message);
        // 当接受到消息时，可以通过 WebSocketClient 发送消息
        webSocketClient.send(message); // 发送给 WebSocketClient
    }

    @OnMessage
    public void onMessage(ByteBuffer message, @PathParam("sid") String sid) {
      //  log.info("收到来自客户端：" + sid + "的信息:" + message);
        // 这里可以处理来自 WebSocketServer 的消息
        webSocketClient.send(message);
    }

    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("连接断开:" + sid);
        sessionMap.remove(sid);
        webSocketClient.onClose(); // 关闭 WebSocketClient
    }

    public static void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }
}