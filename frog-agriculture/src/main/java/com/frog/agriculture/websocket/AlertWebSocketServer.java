package com.frog.agriculture.websocket;

import com.frog.agriculture.domain.SensorAlert;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/alert")
public class AlertWebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(AlertWebSocketServer.class);

    // 使用 ConcurrentHashMap 来存储客户端连接，key 为客户端 ID
    private static final Map<String, AlertWebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    // 与某个客户端的连接会话
    private Session session;

    // 客户端标识
    private String clientId;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;

        // 获取客户端 ID（可以从请求参数或者 header 中获取）
        String clientId = getClientIdFromSession(session);
        this.clientId = clientId;

        log.info("收到新的连接请求，clientId: {}", clientId);  // 日志

        // 如果已存在相同客户端的连接，先关闭旧连接
        AlertWebSocketServer existingConnection = webSocketMap.get(clientId);
        if (existingConnection != null && existingConnection.session.isOpen()) {
            try {
                existingConnection.session.close();
                log.info("关闭客户端 {} 的旧连接", clientId);
            } catch (IOException e) {
                log.error("关闭旧连接失败：" + e.getMessage());
            }
        }

        // 保存新连接
        webSocketMap.put(clientId, this);
        log.info("客户端 {} 连接成功，当前在线数：{}", clientId, webSocketMap.size());
    }

    @OnClose
    public void onClose() {
        if (clientId != null) {
            webSocketMap.remove(clientId);
            log.info("客户端 {} 断开连接，当前在线数：{}", clientId, webSocketMap.size());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到客户端 {} 的消息：{}", clientId, message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("客户端 {} 发生错误：{}", clientId, error.getMessage());
        if (clientId != null) {
            webSocketMap.remove(clientId);
        }
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(SensorAlert alert) {
        Gson gson = new Gson();
        String message = gson.toJson(alert);

        webSocketMap.forEach((clientId, websocket) -> {
            try {
                if (websocket.session.isOpen()) {
                    websocket.session.getBasicRemote().sendText(message);
                    log.debug("推送消息到客户端 {} 成功", clientId);
                } else {
                    log.warn("客户端 {} 连接已关闭，移除连接", clientId);
                    webSocketMap.remove(clientId);
                }
            } catch (IOException e) {
                log.error("推送消息到客户端 {} 失败：{}", clientId, e.getMessage());
                webSocketMap.remove(clientId);
            }
        });
    }

    /**
     * 从 Session 中获取客户端 ID
     */
    private String getClientIdFromSession(Session session) {
        // 可以从 URL 参数中获取
        Map<String, List<String>> params = session.getRequestParameterMap();
        String clientId = null;

        // 获取 clientId 参数值列表
        List<String> clientIds = params.get("clientId");
        if (clientIds != null && !clientIds.isEmpty()) {
            clientId = clientIds.get(0);  // 取第一个值
        }

        // 如果没有传入 clientId，使用 session ID
        if (clientId == null) {
            clientId = session.getId();
        }

        return clientId;
    }
}