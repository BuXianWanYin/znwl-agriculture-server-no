package com.frog.agriculture.websocket;

import com.frog.agriculture.domain.SensorAlert;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/alert")
public class AlertWebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(AlertWebSocketServer.class);

    // 用来存放每个客户端对应的WebSocket对象
    private static final CopyOnWriteArraySet<AlertWebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        log.info("有新的连接加入！当前在线人数为：" + webSocketSet.size());
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        log.info("有连接关闭！当前在线人数为：" + webSocketSet.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息：" + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(SensorAlert alert) {
        Gson gson = new Gson();
        String message = gson.toJson(alert);
        for (AlertWebSocketServer item : webSocketSet) {
            try {
                item.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("推送消息到客户端失败：" + e.getMessage());
            }
        }
    }
}