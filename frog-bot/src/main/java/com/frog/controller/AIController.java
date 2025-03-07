package com.frog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.frog.config.BotConfig;
import com.frog.utils.AIModelApiUtils;
import com.frog.utils.AIModelApiUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.emitter.Emitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.websocket.server.PathParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * ai接口
 */
@RestController
@RequestMapping("/ai")
public class AIController {

    private static final Log log = LogFactory.getLog(AIController.class);

    /**
     * 生成补全
     *
     * @param prompt
     * @return
     */
    @PostMapping(value = "/generate")
    public String generate(String prompt) {
        return AIModelApiUtils.generate(prompt);
    }

    /**
     * 生成补全
     *
     * @param prompt
     * @return
     */
    @PostMapping(value = "/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateStream(String prompt) {
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                CloseableHttpResponse response = AIModelApiUtils.generateStream(prompt);
                InputStream contentInputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(contentInputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("ai流式输出" + line);
                    emitter.send(line); // 直接发送读取的行
                }

                emitter.complete(); // Complete the SSE connection
            } catch (Exception e) {
                log.error("Error in SSE: ", e); // Log the error
                emitter.completeWithError(e); // Handle errors
            }
        }).start();
        return emitter;
    }

    /**
     * 聊天生成
     *
     * @param prompt
     * @return
     */
    @PostMapping(value = "/chatStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(String prompt) {
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                CloseableHttpResponse response = AIModelApiUtils.chatStream(prompt);
                InputStream contentInputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(contentInputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("ai流式输出" + line);
                    JSONObject jsonObj = JSON.parseObject(line);

                    String content = jsonObj.getJSONObject("message").getString("content");

                    // 构建新的 JSON 结构
                    JSONObject newJson = new JSONObject();
                    newJson.put("model", jsonObj.getString("model"));
                    newJson.put("created_at", jsonObj.getString("created_at"));
                    newJson.put("response", content);
                    newJson.put("done", jsonObj.getBoolean("done"));

                    // 发送新格式的数据
                    emitter.send(newJson.toJSONString());
                }

                emitter.complete(); // Complete the SSE connection
            } catch (Exception e) {
                log.error("Error in SSE: ", e); // Log the error
                emitter.completeWithError(e); // Handle errors
            }
        }).start();
        return emitter;
    }

    /**
     * 聊天生成
     *
     * @param prompt
     * @return
     */
    @PostMapping(value = "/chatVLStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatVLStream(String prompt, MultipartFile file) {
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                CloseableHttpResponse response = AIModelApiUtils.chatVLStream(BotConfig.getAiVLUrl() + "chat", prompt, file);
                InputStream contentInputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(contentInputStream, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("ai流式输出" + line);
                    emitter.send(line); // 直接发送读取的行
                }

                emitter.complete(); // Complete the SSE connection
            } catch (Exception e) {
                log.error("Error in SSE: ", e); // Log the error
                emitter.completeWithError(e); // Handle errors
            }
        }).start();
        return emitter;
    }

    /**
     * AI识别
     *
     * @param prompt
     * @return
     */
    @PostMapping(value = "/identify")
    public String identify(String prompt, MultipartFile file) {
        return AIModelApiUtils.chatVLJson(BotConfig.getAiVLUrl() + "identify", prompt, file);
    }


}
