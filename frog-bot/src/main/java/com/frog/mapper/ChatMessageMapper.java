package com.frog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frog.model.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    List<ChatMessage> getChatHistory(@Param("userId") String userId);
}
