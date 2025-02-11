package com.frog.controller;

import com.frog.model.ChatMessage;
import com.frog.service.ChatMessageService;
import com.frog.service.impl.ChatMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
@Slf4j
public class ChatMessageController {

    @Autowired
    private ChatMessageServiceImpl chatMessageServiceimpl;

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@RequestParam String userId) {
        try {
            List<ChatMessage> history = chatMessageServiceimpl.getChatHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("获取聊天历史失败: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/message")
    public ResponseEntity<ChatMessage> saveMessage(@RequestBody ChatMessage message) {
        try {
            // 直接使用 MyBatis-Plus 的 save 方法
            chatMessageServiceimpl.saveMessage(message);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            log.error("保存聊天消息失败: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}