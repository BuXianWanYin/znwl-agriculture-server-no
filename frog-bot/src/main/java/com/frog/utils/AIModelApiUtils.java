package com.frog.utils;

import com.frog.config.BotConfig;
import com.frog.controller.AIController;
import com.frog.config.BotConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AIModelApiUtils {

    private final static String URL = BotConfig.getAiUrl() + "/" + BotConfig.getAiDoc();

    private static Map paramMap = new HashMap<String,Object>();

    private static final Log log = LogFactory.getLog(AIModelApiUtils.class);


    static{
        paramMap.put("model", BotConfig.getAiModel());
        paramMap.put("stream",BotConfig.isStream());
    }

    /**
     * 生成补全
     * @return
     */
    public static String generate(String prompt){
        try {
            paramMap.put("prompt",prompt);
            return HttpClientUtil.doPost4Json(URL, paramMap);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 生成补全流式
     * @return
     */
    public static CloseableHttpResponse generateStream(String prompt){
        try {
            paramMap.put("prompt",prompt);
            Map options = new HashMap<String,Object>();
            paramMap.put("options",options);
            options.put("temperature",0.2);
            options.put("top_p",0.7);
            options.put("repeat_penalty",1.2);
            options.put("mirostat_tau",3.0);
            paramMap.put("options",options);
            return HttpClientUtil.doPost4JsonStream(URL, paramMap);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 聊天流式
     *
     * @return
     */
    public static CloseableHttpResponse chatStream(String prompt) {
        try {
            ArrayList<Object> messages = new ArrayList<Object>();
            Map info = new HashMap<String, String>();
            info.put("system ", "你的名字为朱斌博老师,你是一名农业专家,你可以回答各种农业问题。");
            info.put("role", "user");
            info.put("content", prompt);
            messages.add(info);
            paramMap.put("messages", messages);
            Map options = new HashMap<String,Object>();
            paramMap.put("options",options);
            options.put("temperature",0.2);
            options.put("top_p",0.7);
            options.put("repeat_penalty",1.2);
            options.put("mirostat_tau",3.0);
            paramMap.put("options",options);
            return HttpClientUtil.doPost4JsonStream(URL, paramMap);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }
}
