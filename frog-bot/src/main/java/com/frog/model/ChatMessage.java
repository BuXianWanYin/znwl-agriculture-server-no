package com.frog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "chat_message")
public class ChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String type;

    private String content;

    @TableField("audio_url")
    private String audioUrl;

    private Long timestamp;

    @TableField("user_id")
    private String userId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}