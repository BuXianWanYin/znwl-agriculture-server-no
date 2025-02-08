package com.frog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * bot配置
 */
@Component
@ConfigurationProperties(prefix = "bot")
public class BotConfig {

    /** ai模型 */
    private static String aiModel;

    /** ai地址 */
    private static String aiUrl;

    /** doc */
    private static String aiDoc;

    /** 是否流式 */
    private static boolean stream;

    /** 最长请求时间 */
    private static int aiTimeoutMsec;

    /** asr地址 */
    private static String asrUrl;

    private static String ttsUrl;

    private static String ttsMode;

    private static String spkId;

    private static String promptText;

    private static String promptWav;

    private static String instructText;

    public void setAiModel(String aiModel) {
        BotConfig.aiModel = aiModel;
    }

    public void setAiUrl(String aiUrl) {
        BotConfig.aiUrl = aiUrl;
    }

    public void setAiDoc(String aiDoc) {
        BotConfig.aiDoc = aiDoc;
    }

    public void setStream(boolean stream) {
        BotConfig.stream = stream;
    }

    public void setAiTimeoutMsec(int aiTimeoutMsec) {
        BotConfig.aiTimeoutMsec = aiTimeoutMsec;
    }

    public static String getAiModel() {
        return aiModel;
    }

    public static String getAiUrl() {
        return aiUrl;
    }

    public static String getAiDoc() {
        return aiDoc;
    }

    public static boolean isStream() {
        return stream;
    }

    public static int getAiTimeoutMsec() {
        return aiTimeoutMsec;
    }

    public static String getAsrUrl() {
        return asrUrl;
    }

    public void setAsrUrl(String asrUrl) {
        BotConfig.asrUrl = asrUrl;
    }

    public static String getTtsUrl() {
        return ttsUrl;
    }

    public void setTtsUrl(String ttsUrl) {
        BotConfig.ttsUrl = ttsUrl;
    }

    public static String getTtsMode() {
        return ttsMode;
    }

    public void setTtsMode(String ttsMode) {
        BotConfig.ttsMode = ttsMode;
    }

    public static String getSpkId() {
        return spkId;
    }

    public void setSpkId(String spkId) {
        BotConfig.spkId = spkId;
    }

    public static String getPromptText() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        BotConfig.promptText = promptText;
    }

    public static String getPromptWav() {
        return promptWav;
    }

    public void setPromptWav(String promptWav) {
        BotConfig.promptWav = promptWav;
    }

    public static String getInstructText() {
        return instructText;
    }

    public void setInstructText(String instructText) {
        BotConfig.instructText = instructText;
    }
}
