package com.frog.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frog.common.core.domain.model.AIStandardJobDTO;
import com.frog.config.BotConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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
     * 聊天流式
     *
     * @return
     */
    public static String chatVLJson(String url, String prompt, MultipartFile file) {
        try {
            Map paramMap = new HashMap<String, Object>();
            paramMap.put("prompt_text", prompt);
            return HttpClientUtil.doPost4Json(url, paramMap, file);
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


    /**
     * AI分析参数获取方法
     *
     * @param speciesId   鱼类ID
     * @param germplasmId 种类ID
     * @param typeName    类型名称(鱼/种类)
     * @param name        名称(鱼/菜名)
     * @return AI分析结果JSON
     */
    public static String aiAnalysisJson(Long speciesId, Long germplasmId, String typeName, String name) {
        // 判断类型
        boolean isFish = true;
        if (speciesId != null) {
            isFish = true;
        }
        if (germplasmId != null) {
            isFish = false;
        }

        name = typeName + name;

        // 构建专家角色和模板
        String systemRole = isFish ? "水产养殖专家" : "农业种植专家";
        Map<String, Object> paramsTemplate = new LinkedHashMap<>(); // 使用LinkedHashMap保持顺序

        if (isFish) {
            // 1. 环境参数
            Map<String, Object> envParams = new LinkedHashMap<>();
            envParams.put("水温(℃)", Arrays.asList("标准范围", "说明"));
            envParams.put("pH值", Arrays.asList("标准范围", "说明"));
            envParams.put("溶解氧(mg/L)", Arrays.asList("标准范围", "说明"));
            envParams.put("氨氮(mg/L)", Arrays.asList("标准范围", "说明"));
            envParams.put("亚硝酸盐(mg/L)", Arrays.asList("标准范围", "说明"));
            paramsTemplate.put("环境参数", envParams);

            // 2. 养殖建议
            List<Map<String, String>> suggestions = new ArrayList<>();
            suggestions.add(createSuggestion("水质管理", "具体建议"));
            suggestions.add(createSuggestion("投喂管理", "具体建议"));
            suggestions.add(createSuggestion("疾病防控", "具体建议"));
            suggestions.add(createSuggestion("环境监控", "具体建议"));
            paramsTemplate.put("养殖建议", suggestions);

            // 3. 核心指标
            Map<String, String> coreIndicators = new LinkedHashMap<>();
            coreIndicators.put("生长速度", "百分比");
            coreIndicators.put("抗病能力", "百分比");
            coreIndicators.put("饲料转化率", "百分比");
            coreIndicators.put("市场认可度", "百分比");
            paramsTemplate.put("核心指标", coreIndicators);

            // 4. 综合评估
            Map<String, String> assessment = new LinkedHashMap<>();
            assessment.put("生长评估", "详细说明");
            assessment.put("养殖难度", "详细说明");
            assessment.put("综合建议", "详细说明");
            assessment.put("市场分析", "详细说明");
            paramsTemplate.put("综合评估", assessment);
        } else {
            // 1. 环境参数
            Map<String, Object> envParams = new LinkedHashMap<>();
            envParams.put("温度(°C)", Arrays.asList("标准范围", "说明"));
            envParams.put("湿度(%)", Arrays.asList("标准范围", "说明"));
            envParams.put("光照(lux)", Arrays.asList("标准范围", "说明"));
            envParams.put("风向", Arrays.asList("标准范围", "说明"));
            envParams.put("风速(m/s)", Arrays.asList("标准范围", "说明"));
            envParams.put("pH值", Arrays.asList("标准范围", "说明"));
            paramsTemplate.put("环境参数", envParams);

            // 2. 种植建议
            List<Map<String, String>> suggestions = new ArrayList<>();
            suggestions.add(createSuggestion("水分管理", "具体建议"));
            suggestions.add(createSuggestion("肥料管理", "具体建议"));
            suggestions.add(createSuggestion("病虫害防治", "具体建议"));
            suggestions.add(createSuggestion("环境监控", "具体建议"));
            paramsTemplate.put("种植建议", suggestions);

            // 3. 核心指标
            Map<String, String> coreIndicators = new LinkedHashMap<>();
            coreIndicators.put("生长速度", "百分比");
            coreIndicators.put("抗病能力", "百分比");
            coreIndicators.put("肥料转化率", "百分比");
            coreIndicators.put("市场认可度", "百分比");
            paramsTemplate.put("核心指标", coreIndicators);

            // 4. 综合评估
            Map<String, String> assessment = new LinkedHashMap<>();
            assessment.put("生长评估", "详细说明");
            assessment.put("种植难度", "详细说明");
            assessment.put("综合建议", "详细说明");
            assessment.put("市场分析", "详细说明");
            paramsTemplate.put("综合评估", assessment);
        }

        // 构建消息
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new LinkedHashMap<>();
        systemMessage.put("role", "system");
        try {
            // 使用StringBuilder构建格式化的content
            StringBuilder contentBuilder = new StringBuilder()
                    .append("\n                你是一名").append(systemRole)
                    .append("。请提供").append(name).append("的具体")
                    .append(isFish ? "养殖" : "种植").append("参数范围。\n")
                    .append("                中文说明、严格按照格式返回数据类型。\n")
                    .append("                请按以下格式输出：\n")
                    .append("                ")
                    .append(new ObjectMapper()
                            .writerWithDefaultPrettyPrinter()
                            .writeValueAsString(paramsTemplate))
                    .append("\n                ");

            systemMessage.put("content", contentBuilder.toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        messages.add(systemMessage);

        Map<String, String> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", name);
        messages.add(userMessage);
        paramMap.put("messages", messages);
        paramMap.put("stream", false);

        // 构建格式化要求
        Map<String, Object> format = new LinkedHashMap<>();  // 使用LinkedHashMap保持顺序
        format.put("type", "object");

        // properties
        Map<String, Object> properties = new LinkedHashMap<>();

        // 1. environmental_params
        Map<String, Object> envParamsFormat = new LinkedHashMap<>();
        envParamsFormat.put("type", "object");
        Map<String, Object> envProperties = new LinkedHashMap<>();

        if (isFish) {
            // 水产养殖参数格式 - 按顺序添加
            envProperties.put("water_temperature", createArrayStringFormat());
            envProperties.put("ph", createArrayStringFormat());
            envProperties.put("dissolved_oxygen", createArrayStringFormat());
            envProperties.put("ammonia_nitrogen", createArrayStringFormat());
            envProperties.put("nitrite", createArrayStringFormat());
        } else {
            // 农业种植参数格式 - 按顺序添加
            envProperties.put("temperature", createArrayStringFormat());
            envProperties.put("humidity", createArrayStringFormat());
            envProperties.put("light", createArrayStringFormat());
            envProperties.put("wind_direction", createArrayStringFormat());
            envProperties.put("wind_speed", createArrayStringFormat());
            envProperties.put("ph", createArrayStringFormat());
        }
        envParamsFormat.put("properties", envProperties);
        properties.put("environmental_params", envParamsFormat);

        // 2. breeding/planting_suggestions
        String suggestionsKey = isFish ? "breeding_suggestions" : "planting_suggestions";
        properties.put(suggestionsKey, createSuggestionsFormat());

        // 3. core_indicators
        Map<String, Object> coreIndicatorsFormat = new LinkedHashMap<>();
        coreIndicatorsFormat.put("type", "object");
        Map<String, Object> coreProperties = new LinkedHashMap<>();
        coreProperties.put("growth_rate", createIntegerFormat());
        coreProperties.put("disease_resistance", createIntegerFormat());
        coreProperties.put(isFish ? "feed_conversion" : "fertilizer_conversion", createIntegerFormat());
        coreProperties.put("market_acceptance", createIntegerFormat());
        coreIndicatorsFormat.put("properties", coreProperties);
        properties.put("core_indicators", coreIndicatorsFormat);

        // 4. comprehensive_assessment
        Map<String, Object> assessmentFormat = new LinkedHashMap<>();
        assessmentFormat.put("type", "object");
        Map<String, Object> assessmentProperties = new LinkedHashMap<>();
        assessmentProperties.put("growth_assessment", createStringFormat());
        assessmentProperties.put(isFish ? "breeding_difficulty" : "cultivation_difficulty", createStringFormat());
        assessmentProperties.put("general_recommendations", createStringFormat());
        assessmentProperties.put("market_analysis", createStringFormat());
        assessmentFormat.put("properties", assessmentProperties);
        properties.put("comprehensive_assessment", assessmentFormat);

        format.put("properties", properties);
        format.put("required", Arrays.asList(
                "environmental_params",
                suggestionsKey,
                "core_indicators",
                "comprehensive_assessment"
        ));

        paramMap.put("format", format);
        // 发送请求
        try {
            return HttpClientUtil.doPost4Json(BotConfig.getAiUrl() + "/" + BotConfig.getAiDoc(), paramMap);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    // 辅助方法
    private static Map<String, String> createSuggestion(String type, String content) {
        Map<String, String> suggestion = new LinkedHashMap<>();  // 使用LinkedHashMap保持顺序
        suggestion.put("类型", type);    // 类型放在前面
        suggestion.put("内容", content);  // 内容放在后面
        return suggestion;
    }

    private static Map<String, Object> createArrayStringFormat() {
        Map<String, Object> format = new LinkedHashMap<>();
        format.put("type", "array");
        Map<String, String> items = new LinkedHashMap<>();
        items.put("type", "string");
        format.put("items", items);
        return format;
    }

    private static Map<String, String> createStringFormat() {
        Map<String, String> format = new LinkedHashMap<>();
        format.put("type", "string");
        return format;
    }

    private static Map<String, String> createIntegerFormat() {
        Map<String, String> format = new LinkedHashMap<>();
        format.put("type", "integer");
        return format;
    }

    private static Map<String, Object> createSuggestionsFormat() {
        Map<String, Object> format = new LinkedHashMap<>();
        format.put("type", "array");
        Map<String, Object> items = new LinkedHashMap<>();
        items.put("type", "object");
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("type", createStringFormat());
        properties.put("content", createStringFormat());
        items.put("properties", properties);
        format.put("items", items);
        return format;
    }
}
