package com.frog.utils;

import com.frog.common.core.domain.model.AIStandardJobDTO;
import com.frog.config.BotConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AIModelApiUtils {

    private final static String URL = BotConfig.getAiUrl() + "/" + BotConfig.getAiDoc();

    private static Map paramMap = new HashMap<String, Object>();

    private static final Log log = LogFactory.getLog(AIModelApiUtils.class);


    static {
        paramMap.put("model", BotConfig.getAiModel());
        paramMap.put("stream", BotConfig.isStream());
    }

    /**
     * 生成补全
     *
     * @return
     */
    public static String generate(String prompt) {
        try {
            paramMap.put("prompt", prompt);
            return HttpClientUtil.doPost4Json(URL, paramMap);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 生成补全流式
     *
     * @return
     */
    public static CloseableHttpResponse generateStream(String prompt) {
        try {
            paramMap.put("prompt", prompt);
            Map options = new HashMap<String, Object>();
            paramMap.put("options", options);
            options.put("temperature", 0.2);
            options.put("top_p", 0.7);
            options.put("repeat_penalty", 1.2);
            options.put("mirostat_tau", 3.0);
            paramMap.put("options", options);
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
            info.put("role ", "system");
            info.put("content", "你的名字为朱斌博老师,你是一名农业专家,你可以回答各种农业问题。");
            info.put("role", "user");
            info.put("content", prompt);
            messages.add(info);
            paramMap.put("messages", messages);
            Map options = new HashMap<String, Object>();
            options.put("temperature", 0.2);
            options.put("top_p", 0.7);
            options.put("repeat_penalty", 1.2);
            options.put("mirostat_tau", 3.0);
            paramMap.put("options", options);
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
    public static CloseableHttpResponse chatVLStream(String url, String prompt, MultipartFile file) {
        try {
            Map paramMap = new HashMap<String, Object>();
            paramMap.put("prompt_text", prompt);
            return HttpClientUtil.doPostStream(url, paramMap, file);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }


    /**
     * ai处理物种周期
     *
     * @param aiStandardJobDTO
     */
    public static String aiInsertStandardJobJson(AIStandardJobDTO aiStandardJobDTO) {
        try {
            ArrayList<Object> messages = new ArrayList<Object>();
            Map sysInfo = new HashMap<String, String>();
            sysInfo.put("role", "system");
            sysInfo.put("content", "你是一位经验丰富的物种专家，请提供物种生长过程中各个阶段的名称（中文）和周期，时间为诞生到销售，cycUnit 0表示天单位 1表示周单位");
            if (aiStandardJobDTO.getType() == 0) {
                sysInfo.put("content", "你是一位经验丰富的农业专家，请提供种类生长过程中各个阶段的名称（中文）和周期，时间为诞生到销售，cycUnit 0表示天单位 1表示周单位");
            }
            if (aiStandardJobDTO.getType() == 1) {
                sysInfo.put("content", "你是一位经验丰富的养殖专家，请提供鱼类生长过程中各个阶段的名称（中文）和周期，时间为诞生到销售，cycUnit 0表示天单位 1表示周单位");
            }
            Map userInfo = new HashMap<String, String>();
            userInfo.put("role", "user");
            userInfo.put("content", aiStandardJobDTO.getTypeName() + aiStandardJobDTO.getName());
            messages.add(sysInfo);
            messages.add(userInfo);
            paramMap.put("messages", messages);
            Map<String, Object> format = new HashMap<>();

            // 创建properties对象
            Map<String, Object> properties = new HashMap<>();

            // 创建species属性定义
            Map<String, String> speciesDef = new HashMap<>();
            speciesDef.put("type", "string");
            properties.put("species", speciesDef);

            // 创建jobs属性定义
            Map<String, Object> jobsDef = new HashMap<>();
            jobsDef.put("type", "array");

            // 创建items定义
            Map<String, Object> items = new HashMap<>();
            Map<String, Object> itemProperties = new HashMap<>();

            // 创建jobName属性定义
            Map<String, String> jobNameDef = new HashMap<>();
            jobNameDef.put("type", "string");
            itemProperties.put("jobName", jobNameDef);

            // 创建cycUnit属性定义
            Map<String, Object> cycUnitDef = new HashMap<>();
            cycUnitDef.put("type", "integer");
            cycUnitDef.put("enum", Arrays.asList(0, 1));
            itemProperties.put("cycUnit", cycUnitDef);

            // 创建jobStart和jobFinish属性定义
            Map<String, String> jobStartDef = new HashMap<>();
            jobStartDef.put("type", "integer");
            itemProperties.put("jobStart", jobStartDef);

            Map<String, String> jobFinishDef = new HashMap<>();
            jobFinishDef.put("type", "integer");
            itemProperties.put("jobFinish", jobFinishDef);

            // 设置items中的properties
            items.put("type", "object");
            items.put("properties", itemProperties);
            items.put("required", Arrays.asList("jobName", "cycUnit", "jobStart", "jobFinish"));
            jobsDef.put("items", items);
            properties.put("jobs", jobsDef);

            // 组装最终的format对象
            format.put("type", "object");
            format.put("properties", properties);
            format.put("required", Arrays.asList("species", "jobs"));

            paramMap.put("format", format);
            return HttpClientUtil.doPost4Json(BotConfig.getAiUrl() + "/" + BotConfig.getAiDoc(), paramMap);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }
}
