package com.frog.controller;

import com.frog.config.BotConfig;
import com.frog.utils.AIModelApiUtils;
import com.frog.utils.AIModelApiUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.emitter.Emitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.websocket.server.PathParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
     * @param prompt
     * @return
     */
    @PostMapping(value = "/generate")
    public String generate(String prompt) {
        return AIModelApiUtils.generate(prompt);
    }

     /**
      * 生成补全
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
}
