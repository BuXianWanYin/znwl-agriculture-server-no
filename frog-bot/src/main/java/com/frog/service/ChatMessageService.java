package com.frog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.frog.model.ChatMessage;

import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {
    List<ChatMessage> getChatHistory(String userId);
}