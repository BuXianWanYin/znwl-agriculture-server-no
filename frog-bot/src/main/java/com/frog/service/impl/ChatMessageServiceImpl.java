package com.frog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frog.mapper.ChatMessageMapper;
import com.frog.model.ChatMessage;
import com.frog.service.ChatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Override
    public List<ChatMessage> getChatHistory(String userId) {
        try {
            // 使用 MyBatis-Plus 的查询构造器
            return this.lambdaQuery()
                    .eq(ChatMessage::getUserId, userId)
                    .orderByAsc(ChatMessage::getTimestamp)
                    .list();
        } catch (Exception e) {
            log.error("获取聊天历史记录失败: ", e);
            throw new RuntimeException("获取聊天历史记录失败");
        }
    }

    public ChatMessage saveMessage(ChatMessage message) {
        try {
            // 设置时间戳（如果未设置）
            if (message.getTimestamp() == null) {
                message.setTimestamp(System.currentTimeMillis());
            }

            // 使用 MyBatis-Plus 的 save 方法
            this.save(message);
            return message;
        } catch (Exception e) {
            log.error("保存聊天消息失败: ", e);
            throw new RuntimeException("保存聊天消息失败");
        }
    }
}