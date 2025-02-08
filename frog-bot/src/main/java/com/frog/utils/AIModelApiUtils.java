package com.frog.utils;

import com.frog.config.BotConfig;
import com.frog.controller.AIController;
import com.frog.config.BotConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
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
            return HttpClientUtil.doPost4JsonStream(URL, paramMap);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }
}
