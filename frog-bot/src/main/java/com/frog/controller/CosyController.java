package com.frog.controller;

import com.frog.config.BotConfig;
import com.frog.utils.HttpClientUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.xml.crypto.Data;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * cosy接口
 */
@RestController
@RequestMapping("/cosy")
public class CosyController {

    private static final int SAMPLE_RATE = 22050; // 采样率

    private static final int BITS_PER_SAMPLE = 16; // 采样位数

    private static final int CHANNELS = 1; // 单声道

    private static final String wavPath = "wav";

    private static final Logger log = LogManager.getLogger(CosyController.class);

    /**
     * 语音合成接口
     */
    @PostMapping(value = "/voice")
    public String cosyVoice(String text) {
        log.info(text);

        Map<String, String> payload = new HashMap<>();
        String url = BotConfig.getTtsUrl();  // Adjust this to the correct URL for your service.

        String mode = BotConfig.getTtsMode();
        payload.put("tts_text", text);
        switch (mode) {
            case "sft":
                payload.put("spk_id", BotConfig.getSpkId());
                break;
            case "zero_shot":
                payload.put("prompt_text", BotConfig.getPromptText());
                break;
            case "cross_lingual":
                break;
            default: // Handle default case
                payload.put("spk_id", BotConfig.getSpkId());
                payload.put("instruct_text", BotConfig.getInstructText());
                break;
        }

       try {
           byte[] response = HttpClientUtil.doPostRestTemplateByte(url + mode, payload, BotConfig.getPromptWav(), mode);
           if (response != null && response.length > 0) {
               // 将字节数组转换为int16数组
               short[] audioData = new short[response.length / 2];
               ByteBuffer.wrap(response).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(audioData);

               // 创建WAV文件格式
               AudioFormat format = new AudioFormat(
                       SAMPLE_RATE,          // 采样率
                       BITS_PER_SAMPLE,      // 采样位数
                       CHANNELS,             // 声道数
                       true,                 // 是否有符号（signed）
                       false                 // 字节序（false=小端序）
               );

               // 创建音频流
               byte[] wavData = new byte[audioData.length * 2];
               ByteBuffer.wrap(wavData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(audioData);

               AudioInputStream audioInputStream = new AudioInputStream(
                       new ByteArrayInputStream(wavData),
                       format,
                       audioData.length
               );

               File file = new File(wavPath);
               if (!file.exists()){
                   file.mkdir();
               }
               //截取文件名
                String wavName = text.substring(0,7);
               // 创建时间戳
               String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

               // 组合文件名
               String fileName = wavName + "-" + timestamp;
               // 保存为WAV文件
               AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(wavPath + "/" + fileName + ".wav"));
               System.out.println("音频文件已保存到wav文件夹下 文件名：" + fileName + ".wav");
           }
       }catch (Exception e){
          log.error(e.getMessage());
       }
        return "成功";
    }

    /**
     * 语音合成接口
     */
    @PostMapping(value = "/voicepath")
    public String cosyVoicePath(
            @RequestParam("timestamp") String timestamp,
            @RequestParam("text") String text) {

        log.info("Received text: " + text);
        log.info("Received timestamp: " + timestamp);

        // 验证时间戳格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setLenient(false); // 不宽松解析
        Date date;
        try {
            date = dateFormat.parse(timestamp); // 解析时间戳
        } catch (ParseException e) {
            log.error("Invalid timestamp format: " + timestamp);
            return "时间戳格式无效"; // 返回错误信息
        }

        Map<String, String> payload = new HashMap<>();
        String url = BotConfig.getTtsUrl();  // 适当调整为您的服务的正确 URL。

        String mode = BotConfig.getTtsMode();
        payload.put("tts_text", text);
        switch (mode) {
            case "sft":
                payload.put("spk_id", BotConfig.getSpkId());
                break;
            case "zero_shot":
                payload.put("prompt_text", BotConfig.getPromptText());
                break;
            case "cross_lingual":
                break;
            default: // 处理默认情况
                payload.put("spk_id", BotConfig.getSpkId());
                payload.put("instruct_text", BotConfig.getInstructText());
                break;
        }

        try {
            byte[] response = HttpClientUtil.doPostRestTemplateByte(url + mode, payload, BotConfig.getPromptWav(), mode);
            if (response != null && response.length > 0) {
                // 将字节数组转换为 int16 数组
                short[] audioData = new short[response.length / 2];
                ByteBuffer.wrap(response).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(audioData);

                // 创建 WAV 文件格式
                AudioFormat format = new AudioFormat(
                        SAMPLE_RATE,          // 采样率
                        BITS_PER_SAMPLE,      // 采样位数
                        CHANNELS,             // 声道数
                        true,                 // 是否有符号（signed）
                        false                 // 字节序（false=小端序）
                );

                // 创建音频流
                byte[] wavData = new byte[audioData.length * 2];
                ByteBuffer.wrap(wavData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(audioData);

                AudioInputStream audioInputStream = new AudioInputStream(
                        new ByteArrayInputStream(wavData),
                        format,
                        audioData.length
                );

                File file = new File(wavPath);
                if (!file.exists()){
                    file.mkdir();
                }

                // 截取文件名
                String wavName = text.length() > 7 ? text.substring(0, 7) : text; // 防止字符串过短

                // 组合文件名（使用传入的时间戳）
                String fileName = wavName + "-" + timestamp + ".wav";
                // 保存为 WAV 文件
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(wavPath + "/" + fileName));
                System.out.println("音频文件已保存到wav文件夹下 文件名：" + fileName);

                // 返回音频文件的访问 URL
                return "http://10.0.28.47:8081/wav/" + fileName;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "失败"; // 如果出错，则返回失败信息
    }
}